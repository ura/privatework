package book;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Normalizer;
import util.WinRARWrapper;
import util.file.DirCollector;
import util.file.FileOperationUtil;
import util.file.NameUtil;
import book.webapi.BookInfo;
import book.webapi.BookInfo.TYPE;
import book.webapi.BookInfoFromWeb;
import collection.Tuple;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import conf.ConfConst;
import static util.file.FileNameUtil.createPath;
import static util.file.FileNameUtil.getExt;
import static util.file.FileNameUtil.getFileName;

public class BookNameUtil implements NameUtil {

	private static Logger log = LoggerFactory.getLogger(BookNameUtil.class);

	private static final int THREAD_GET_BOOKINFO = ConfConst.MAIN_CONF
			.getInt(ConfConst.THREAD_GET_BOOKINFO);

	private static Pattern parterm = Pattern.compile("第([0-9]+)巻");
	private static Pattern partermEng = Pattern.compile("v([0-9]+)\\.");
	private static Pattern partermEng2 = Pattern.compile("v([0-9]+)_");
	private static Pattern partermSimple = Pattern.compile(" ([0-9]+)\\.");

	private static Pattern partermBad1 = Pattern
			.compile("[^0-9\\-]{1,2}([0-9]{1,2})$");
	private static Pattern partermBad2 = Pattern
			.compile("[^0-9\\-]{1,2}([0-9]{1,2})[^0-9\\-]{1,2}");

	private static List<Pattern> regList = new ArrayList<Pattern>();
	static {
		regList.add(parterm);
		regList.add(partermEng);
		regList.add(partermEng2);
		regList.add(partermSimple);
		regList.add(partermBad1);
		regList.add(partermBad2);

	}

	private static Pattern folderNameIsbn = Pattern
			.compile("\\[ISBN([0-9A-Za-z ]*)\\]");
	private static List<Pattern> regIsbnList = new ArrayList<Pattern>();
	static {
		regIsbnList.add(folderNameIsbn);

	}

	private static Pattern partermAll = Pattern.compile("全.*([0-9]+)");

	/**
	 * 日付を考慮した正規表現　日付は [] で囲まれている場合多し。
	 */
	private static Pattern partermBetween = Pattern
			.compile("[^\\[0-9]([0-9]{1,2})[-]+([0-9]{2})[^\\]]+");

	private static List<String> REPLASE_LIST = new ArrayList<>();
	static {
		REPLASE_LIST
				.add("[^\\u4E00-\\u9FBF\\u0020-\\u007Ea-zA-Z0-9０-９　 \\u3040-\\u309F\\u30A0-\\u30FF\\u30A0-\\u30FF\\u30A0-\\u30FF]");
		REPLASE_LIST
				.add("_[\\[\\]\\u4E00-\\u9FBF\\u0020-\\u007Ea-zA-Z0-9０-９　 \\u3040-\\u309F\\u30A0-\\u30FF\\u30A0-\\u30FF\\u30A0-\\u30FF]_");
		REPLASE_LIST
				.add("_[\\[\\]\\u4E00-\\u9FBF\\u0020-\\u007Ea-zA-Z0-9０-９　 \\u3040-\\u309F\\u30A0-\\u30FF\\u30A0-\\u30FF\\u30A0-\\u30FF]_");
		REPLASE_LIST.add("^_");

	}

	@Inject
	private BarcodeReader4Book barcodeReader;

	@Inject
	private BookInfoRepo bookInfoRepo;

	/**
	 * 日本語のみの名称に変換します。
	 * シンプルな名前に置換した名称を返します。
	 *
	 */
	private String createSimpleName(File f, String reg) {

		//元が十分に短い場合は変更しない
		if (getFileName(f).length() < 3) {
			return getFileName(f) + "." + getExt(f);
		}

		String replaceAll = Normalizer.normalizer(getFileName(f)).replaceAll(
				reg, "_")
				+ "." + getExt(f);

		return replaceAll.replace("_{2,20}", "_");

	}

	/**
	 * 日本語のみの名称に変換します。
	 * シンプルな名前に置換した名称を返します。
	 * REPLASE_LIST
	 */
	public Map<File, File> createSimpleName(Collection<File> fList) {

		Map<File, File> baseMap = new HashMap<>();
		File sample = null;
		for (File file : fList) {
			baseMap.put(file, file);
			sample = file;
		}

		Map<File, File> result = null;
		for (String rep : REPLASE_LIST) {

			Set<String> set = new HashSet<>();
			Map<File, File> map = new HashMap<>(baseMap);
			for (Entry<File, File> file : map.entrySet()) {

				String createSimpleName = createSimpleName(file.getValue(), rep);
				File dest = createPath(file.getValue().getParentFile(),
						createSimpleName);
				map.put(file.getKey(), dest);
				if (set.contains(dest.getName())) {
					log.warn("重複しています。{} >>  {}", file.getKey(), dest);

				}
				set.add(dest.getName());

			}

			if (fList.size() == set.size()) {
				result = map;
			} else {
				break;
			}
		}

		if (result != null) {
			return result;

		}

		log.error("他のファイルと重複しないファイル名を作成することができませんでした。{}",
				sample.getAbsolutePath());
		throw new IllegalStateException("他のファイルと重複しないファイル名を作成することができませんでした。"
				+ sample.getAbsolutePath());

	}

	/**
	 * バーコード、フォルダ名、その他もろもろの手段を利用して
	 * 書籍情報を取得します。
	 * @param root
	 * @return
	 */
	public Map<File, BookInfo> getAllbookInfo(File root) {
		DirCollector dirs = DirCollector.create(root);

		Map<File, BookInfo> m = new HashMap<>();

		try {
			ExecutorService ex = Executors
					.newFixedThreadPool(THREAD_GET_BOOKINFO);
			List<Callable<Map<File, BookInfo>>> l = new ArrayList<>();
			for (File dir : dirs.dirSet.keySet()) {

				l.add(new GetBookInfoTask(dir));

			}
			List<Future<Map<File, BookInfo>>> invokeAll = ex.invokeAll(l);
			for (Future<Map<File, BookInfo>> future : invokeAll) {

				m.putAll(future.get());
			}

		} catch (InterruptedException e) {
			log.error("例外が発生しました。", e);
			throw new IllegalStateException(e);
		} catch (ExecutionException e) {
			log.error("例外が発生しました。", e);
			throw new IllegalStateException(e);
		}

		return m;

	}

	private class GetBookInfoTask implements Callable<Map<File, BookInfo>> {

		public GetBookInfoTask(File dir) {
			super();

			this.dir = dir;
		}

		private File dir;

		@Override
		public Map<File, BookInfo> call() throws Exception {

			BookInfo bookNo = getBookInfo(dir);

			HashMap<File, BookInfo> hashMap = new HashMap<File, BookInfo>();
			if (bookNo != null) {
				hashMap.put(dir, bookNo);
			}
			return hashMap;
		}

	}

	/**
	 * フォルダ名からISBNを抜き取ります。
	 * @param dir
	 * @return
	 */
	public String getISBNFromFolderName(File dir) {
		String name = dir.getName();
		for (Pattern reg : regIsbnList) {
			Matcher matcher = reg.matcher(name);

			boolean result = matcher.find();

			if (result) {
				String group = matcher.group(1);

				return group;
			}

		}
		return null;
	}

	private static Pattern FOLDER_REG = Pattern
			.compile("[])」】  ]([^])」】]*) (第|v)([0-9]+)(巻|$)");

	/**
	 * 正規化前のフォルダ名より書籍情報を類推します。
	 * @param dir
	 * @return
	 */
	public BookInfo bookInfoFromFolderName(File dir) {
		Matcher matcher = FOLDER_REG.matcher(dir.getName());
		//期待できそう
		if (matcher.find()) {
			String title = matcher.group(1);
			String no = matcher.group(3);

			//dir.getName().split("[]「」『』【】 　");

			BookInfo bookInfoFromTitle = BookInfoFromWeb.getBookInfoFromTitle(
					title, no);
			if (bookInfoFromTitle != null) {
				log.warn("フォルダ名より書籍情報を推測しました。{} >> {}", dir.getName(),
						bookInfoFromTitle.getInfo());
				return bookInfoFromTitle;
			}

		}
		return null;
		//TODO 巻数がないバージョンも作ること。
		//フォルダから巻　コミック等の値を抜く
		//APIに突っ込んでみる
	}

	/**
	 * バーコードスキャンをして、書籍情報を取得します。
	 * スキャンした結果はレポジトリに書き込みます。
	 * @param dir
	 * @return
	 */
	public BookInfo getBookInfo(File dir) {

		//TODO INFOにする
		log.warn("書籍名取得処理を開始します{}", dir.getAbsolutePath());

		if ((BookInfo.isBookInfoName(dir.getParentFile()) || BookInfo
				.isBookInfoName(dir.getParentFile().getParentFile()))) {
			log.warn("親フォルダが書籍情報のため、スキップします。。{}", dir);
			return null;
		}

		if (true && BookInfo.isBookInfoName(dir)) {
			log.warn("フォルダ名より、処理済みのフォルダと認識したため、そのまま情報を使用します。{}",
					dir.getAbsolutePath());
			BookInfo bookInfo = BookInfo.createBookInfo(dir);
			bookInfoRepo.addHave(bookInfo);
			return bookInfo;
		}
		if (true) {
			BookInfo bookInfoFromFolder = bookInfoFromFolderName(dir);
			if (bookInfoFromFolder != null) {
				bookInfoRepo.addHave(bookInfoFromFolder);
				return bookInfoFromFolder;
			}
		}

		String barcode = getISBNFromFolderName(dir);
		if (barcode == null) {
			//雑誌が入っていたら、バーコード見ても無駄。
			if (!dir.getAbsolutePath().contains("雑誌")) {
				barcode = barcodeReader.autoReadDir(dir);
			}
		}
		if (barcode != null) {

			BookInfo info = BookInfoFromWeb.getBookInfo(barcode);
			if (info != null) {
				bookInfoRepo.addHave(info);
				return info;
			} else {

				log.warn("ISBNより書籍情報が取得出来なかったので、フォルダ名を返します。{}", dir);

				return new BookInfo(dir.getName());
			}

		} else {

			log.warn("バーコード情報が取得出来なかったので、フォルダ名を返します。{}", dir);

			return new BookInfo(dir.getName());
		}

	}

	public String bookNo(String no) {

		for (Pattern reg : regList) {
			Matcher matcher = reg.matcher(no);

			boolean result = matcher.find();
			log.info(no + "\t" + reg.pattern() + "\t" + result);

			if (result) {
				String group = matcher.group(1);

				return no_XX(group);
			}

		}

		throw new IllegalArgumentException(no);

	}

	private static String no_XX(String xx) {

		return no_XX(Integer.parseInt(xx));

	}

	private static String no_XX(int x) {
		DecimalFormat nf = new DecimalFormat("##");
		nf.setMinimumIntegerDigits(2);
		return nf.format(x);

	}

	/**
	 * 巻数情報をまとめ直す。
	 * 1,2,5,6,7,10,12が入力だった場合、
	 * 1-2
	 * 5-7
	 * 10
	 * 12
	 * とする。
	 *
	 * @param sortedSet
	 * @return
	 */
	public Set<String> getNO(SortedSet<BookInfo> sortedSet) {

		SortedSet<String> set = new TreeSet<>();
		for (BookInfo bookInfo : sortedSet) {

			if (!bookInfo.getNo().equals("")) {
				set.add(bookInfo.getNo());
			}
		}
		if (set.size() == 0) {
			return set;
		}

		int first = Integer.parseInt(set.first());
		int last = Integer.parseInt(set.last());

		int start = -1;
		int end = -1;

		SortedSet<String> result = new TreeSet<>();
		for (int i = first; i <= last; i++) {

			if (set.contains(no_XX(i))) {
				if (start == -1) {
					start = i;
				}
				end = i;

			} else if (start != -1) {

				if (start == end) {
					result.add(no_XX(start));
				} else {
					result.add(no_XX(start) + "-" + no_XX(end));
				}
				start = -1;

			}
		}

		if (start == end) {
			result.add(no_XX(start));
		} else {
			result.add(no_XX(start) + "-" + no_XX(end));
		}

		return result;

	}

	private final static int MAX_REBIULD = 25;

	/**
	 * ファイルをまとめ直し、リビルドする。
	 * @param baseDir
	 * @param map
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void createCominName(File baseDir, SortedMap<BookInfo, File> map)
			throws IOException, InterruptedException {

		SortedMap<Tuple<String, TYPE>, SortedMap<BookInfo, File>> m = classfy(map);

		File temp1 = FileOperationUtil.createTempDir(baseDir, "1ファイル");
		File tempMany = FileOperationUtil.createTempDir(baseDir, "完成");

		for (Entry<Tuple<String, TYPE>, SortedMap<BookInfo, File>> e : m
				.entrySet()) {
			SortedMap<BookInfo, File> value = e.getValue();
			Set<BookInfo> keySet = value.keySet();
			List<BookInfo> list = new ArrayList<BookInfo>(keySet);

			log.warn("基礎名の分類結果です。{} : data数 {}", e.getKey(), keySet.size());

			File path = createPath(baseDir, e.getKey().val1);
			path.delete();
			path.mkdir();
			for (BookInfo bookInfo : list) {
				File file = value.get(bookInfo);
				file.renameTo(createPath(path, file));

			}
			WinRARWrapper.encode(path, path);
			path.renameTo(createPath(tempMany, path.getName()));

			if (list.size() == 1) {
				path.renameTo(createPath(temp1, path.getName()));
			} else {
				path.renameTo(createPath(tempMany, path.getName()));
			}

		}

	}

	/**
	 * 分類のロジックです。
	 * 現在の実装は、著者名-タイトル
	 * による分類で一切のゆらぎを許しません。
	 * @param map
	 * @return
	 */
	protected SortedMap<Tuple<String, TYPE>, SortedMap<BookInfo, File>> classfy(
			SortedMap<BookInfo, File> map) {
		//コミックの名称（巻を除く）ごとに分別
		SortedMap<Tuple<String, TYPE>, SortedMap<BookInfo, File>> m = new TreeMap<Tuple<String, TYPE>, SortedMap<BookInfo, File>>() {
			@Override
			public SortedMap<BookInfo, File> get(Object key) {
				if (!this.containsKey(key)) {
					TreeMap<BookInfo, File> treeMap = new TreeMap<BookInfo, File>();
					this.put((Tuple<String, TYPE>) key, treeMap);
				}
				return super.get(key);
			}

		};
		SortedMap<Tuple<String, TYPE>, SortedMap<BookInfo, File>> result = new TreeMap<Tuple<String, TYPE>, SortedMap<BookInfo, File>>() {
			@Override
			public SortedMap<BookInfo, File> get(Object key) {
				if (!this.containsKey(key)) {
					TreeMap<BookInfo, File> treeMap = new TreeMap<BookInfo, File>();
					this.put((Tuple<String, TYPE>) key, treeMap);
				}
				return super.get(key);
			}

		};

		for (BookInfo bookInfo : map.keySet()) {
			Tuple<String, TYPE> baseInfo;
			if (bookInfo.isRowdateOnly()) {
				baseInfo = Tuple.newT(bookInfo.getRowTitle(), TYPE.ROW);
			} else {
				//巻子がないものは巻数がない物同士でまとめる。
				if (bookInfo.getNo().equals("")) {
					baseInfo = Tuple.newT(bookInfo.getAuthor(), TYPE.ONE);
				} else {
					baseInfo = Tuple.newT(bookInfo.getTitleStr(), TYPE.KAN);
				}
			}

			log.info("１次分類しています。{} >>  {}", baseInfo, bookInfo.getInfo());
			m.get(baseInfo).put(bookInfo, map.get(bookInfo));
		}

		for (Entry<Tuple<String, TYPE>, SortedMap<BookInfo, File>> e1 : m
				.entrySet()) {
			String k1 = e1.getKey().val1;
			TYPE k2 = e1.getKey().val2;

			for (Entry<Tuple<String, TYPE>, SortedMap<BookInfo, File>> e2 : m
					.entrySet()) {
				String k3 = e2.getKey().val1;
				TYPE k4 = e2.getKey().val2;

				if (e1.equals(e2)) {
					continue;
				}

				if (k2 == TYPE.ONE && k4 == TYPE.ONE) {
					if (new Distance().ld(k1, k3) <= 1 || k1.contains(k3)
							|| k3.contains(k1)) {

						move(e1, k1, e2, k3);

					}
				} else if (k2 == TYPE.KAN && k4 == TYPE.KAN) {
					SortedMap<BookInfo, File> value1 = e1.getValue();
					SortedMap<BookInfo, File> value2 = e2.getValue();

					if (value1.isEmpty() || value2.isEmpty()) {
						continue;
					}

					String author1 = value1.firstKey().getAuthor();
					String author2 = value2.firstKey().getAuthor();

					String t1 = value1.firstKey().getTitleStr();
					String t2 = value2.firstKey().getTitleStr();

					if ((new Distance().ld(author1, author2) <= 1
							|| author1.contains(author2) || author2
								.contains(author1))
							&& new Distance().ld(t1, t2) <= 1

					) {

						move(e1, k1, e2, k3);
					}

				} else if (k2 == TYPE.ROW && k4 == TYPE.ROW) {
					if (score(k1, k3) > 90) {
						move(e1, k1, e2, k3);
					}
				}

			}

		}
		renameKey(m, result);
		return result;
	}

	private void renameKey(
			SortedMap<Tuple<String, TYPE>, SortedMap<BookInfo, File>> src,
			SortedMap<Tuple<String, TYPE>, SortedMap<BookInfo, File>> result) {

		for (Entry<Tuple<String, TYPE>, SortedMap<BookInfo, File>> e : src
				.entrySet()) {

			if (e.getValue().size() == 0) {
				continue;
			}

			switch (e.getKey().val2) {
			case ROW:

				Set<BookInfo> keySet = e.getValue().keySet();
				Collection<String> names = Collections2.transform(keySet,
						new Function<BookInfo, String>() {
							@Override
							public String apply(BookInfo input) {

								return input.getRowTitle()
										.replaceAll("[\\.\\[0-9\\-\\]*]", "")
										.trim();
							}
						});

				String prefix = StringUtils.getCommonPrefix(
						names.toArray(new String[0])).trim();
				if (prefix.length() > 1) {
					log.info("下記に名称を変更します。{} {}", prefix, names.size());

					result.put(Tuple.newT(prefix, TYPE.ROW), e.getValue());
				} else {
					log.info("下記に名称変更できませんでした。{} \n{}",
							Joiner.on("\n").join(names), names.size());
				}
				break;

			case KAN:
				SortedMap<BookInfo, File> value = e.getValue();
				SortedMap<BookInfo, File> reSort = Maps
						.newTreeMap(new Comparator<BookInfo>() {

							@Override
							public int compare(BookInfo o1, BookInfo o2) {

								return new CompareToBuilder()
										.append(o1.getNo(), o2.getNo())
										.append(o1.getTitleStr(),
												o2.getTitleStr())
										.toComparison();
							}
						});
				reSort.putAll(value);

				List<List<BookInfo>> partition = Lists.partition(
						new ArrayList<>(reSort.keySet()), MAX_REBIULD);

				for (final List<BookInfo> subList : partition) {
					String folderName;
					if (subList.size() != 1) {
						folderName = createCominName(subList);
					} else {
						folderName = subList.get(0).getBaseInfo();
					}
					SortedMap<BookInfo, File> filterMap = Maps.filterKeys(
							reSort, new Predicate<BookInfo>() {
								@Override
								public boolean apply(BookInfo input) {
									return subList.contains(input);
								}
							});
					result.put(Tuple.newT(folderName, TYPE.KAN), filterMap);

				}

				break;
			case ONE:
				result.put(Tuple.newT("[一般コミック][" + e.getKey().val1 + "]",
						TYPE.ONE), e.getValue());
				break;
			default:
				throw new IllegalArgumentException();

			}

		}

		return;
	}

	/**
	 * 多い方のネーミングに移動する。
	 * @param e1
	 * @param k1
	 * @param e2
	 * @param k3
	 */
	private void move(Entry<Tuple<String, TYPE>, SortedMap<BookInfo, File>> e1,
			String k1,
			Entry<Tuple<String, TYPE>, SortedMap<BookInfo, File>> e2, String k3) {
		SortedMap<BookInfo, File> value1 = e1.getValue();
		SortedMap<BookInfo, File> value2 = e2.getValue();

		if (value1.size() == 0 || value2.size() == 0) {
			return;
		}

		if (value1.size() > value2.size()) {
			value1.putAll(value2);
			value2.clear();
			log.info("2次分類しています。{} >>  {}", k3, k1);
		} else {
			value2.putAll(value1);
			value1.clear();
			log.info("2次分類しています。{} >>  {}", k1, k3);
		}
	}

	public Collection<String> score(String s1, Collection<String> list) {

		Set<String> reList = new HashSet<>();
		for (String s2 : list) {
			if (score(s1, s2) > 90) {
				reList.add(s2);
			}

		}

		return reList;

	}

	/**
	 * カッコつきノ表現があるか判別している。
	 * ただし、日付は除く。
	 * @param name
	 * @return
	 */
	private boolean haveDateStr(String name) {

		Pattern pattern = Pattern.compile("\\[.*[^0-9]{3,20}.*\\]");
		if (name.contains("]") && pattern.matcher(name).find()) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * 単純な比較と[・・・]部分の比較の２パターンで類似チェック。
	 * なお、数字はすべて比較対象にいれにない。
	 * @param s1
	 * @param s2
	 * @return
	 */
	public int score(String s1, String s2) {
		int r1 = scoreCore(s1, s2);
		int r2 = 0;
		if (haveDateStr(s1) && haveDateStr(s2)) {
			r2 = scoreCore(s1.replaceAll("[^]]*$", ""),
					s2.replaceAll("[^]]*$", ""));
		}

		return r1 > r2 ? r1 : r2;
	}

	/**
	 * 90超ならほぼ類似
	 * @param s1
	 * @param s2
	 * @return
	 */
	public int scoreCore(String s1, String s2) {

		try {
			String r1 = s1.replaceAll("[0-9]", "");
			String r2 = s2.replaceAll("[0-9]", "");

			int ld = new Distance().ld(r1, r2);
			int max = r1.length() > r2.length() ? r1.length() : r2.length();
			return (max - ld) * 100 / max;
		} catch (Exception e) {
			return 0;
		}

	}

	/**
	 * 前提条件　baseInfoが揃っているものを入れること。
	 * 著者、出版社、書名、巻数でフォルダ名を作る
	 * @param list
	 * @return
	 */
	public String createCominName(List<BookInfo> list) {

		Collections.sort(list);
		BookInfo sampleinfo = list.get(0);
		log.info("{} 関連の巻数抽出をします", sampleinfo.getBaseInfo());

		if (list.get(0).isRowdateOnly()) {
			return "[一般コミック]" + sampleinfo.getRowTitle();
		} else {
			String baseInfo = sampleinfo.getBaseInfo();
			if (list.size() == 1) {
				log.info("{} 1ファイルしかないので、巻数抽出は不要でした。", sampleinfo.getBaseInfo());
				return "[一般コミック]" + baseInfo;
			} else {
				return "[一般コミック]" + baseInfo + " " + createComicNoStr(list);
			}
		}

	}

	/**
	 * 巻数部分を作成する。
	 * [01-10]
	 * や、
	 * [01-10 09抜け]
	 * など
	 * @param list
	 * @return
	 */
	private String createComicNoStr(List<BookInfo> list) {

		SortedSet<String> set = new TreeSet<String>();
		SortedSet<String> setNG = new TreeSet<String>();

		//ベース名がソロていないので、
		//出版社名の違い等に対応するために
		SortedSet<String> setNO = new TreeSet<String>();
		for (BookInfo b : list) {
			if (!b.getNo().equals("")) {
				setNO.add(b.getNo());
			}
		}

		int noCount = Integer.parseInt(setNO.first());
		int noCountLast = Integer.parseInt(setNO.last());

		for (int i = noCount; i <= noCountLast; i++) {
			String noStr = no_XX(i);
			for (BookInfo bookinfo : list) {
				if (bookinfo.getNo().equals(noStr)) {
					set.add(noStr);
				}

			}
			if (!set.contains(noStr)) {
				setNG.add(noStr);
			}

		}

		String first = set.first();
		String last = set.last();

		StringBuilder sb = new StringBuilder();
		sb.append("第").append(first).append("-").append(last).append("巻");

		for (String string : setNG) {
			sb.append(" ").append(string).append("抜け");

		}

		return sb.toString();

	}

	public BarcodeReader4Book getBarcodeReader() {
		return barcodeReader;
	}

	public void setBarcodeReader(BarcodeReader4Book barcodeReader) {
		this.barcodeReader = barcodeReader;
	}

	public BookInfoRepo getBookInfoRepo() {
		return bookInfoRepo;
	}

	public void setBookInfoRepo(BookInfoRepo bookInfoRepo) {
		this.bookInfoRepo = bookInfoRepo;
	}

}
