package book;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

import module.InjectorMgr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.WinRARWrapper;
import util.file.DirCollector;
import util.file.FileOperationUtil;
import util.file.NameUtil;
import book.webapi.BookInfo;
import book.webapi.BookInfoFromWeb;
import conf.ConfConst;
import static util.file.FileNameUtil.createPath;
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

	private static BarcodeReader4Book barcodeReader = InjectorMgr.get()
			.getInstance(BarcodeReader4Book.class);

	/**
	 * 日本語のみの名称に変換します。
	 * シンプルな名前に置換した名称を返します。
	 *
	 */
	private String createSimpleName(File f, String reg) {

		//元が十分に短い場合は変更しない
		if (getFileName(f).length() < 3) {
			return getFileName(f);
		}

		String replaceAll = getFileName(f).replaceAll(reg, "_");

		return replaceAll.replace("_{2,20}", "_");

	}

	/**
	 * 日本語のみの名称に変換します。
	 * シンプルな名前に置換した名称を返します。
	 * REPLASE_LIST
	 */
	public Map<File, File> createSimpleName(Collection<File> f) {

		Map<File, File> map = new HashMap<>();
		File sample = null;
		for (File file : f) {
			map.put(file, file);
			sample = file;
		}

		Map<File, File> result = null;
		for (String rep : REPLASE_LIST) {

			Set<File> set = new HashSet<>();
			for (Entry<File, File> file : map.entrySet()) {

				String createSimpleName = createSimpleName(file.getValue(), rep);
				File dest = createPath(file.getValue().getParentFile(),
						createSimpleName);
				map.put(file.getKey(), dest);
				set.add(dest);
			}

			if (f.size() == set.size()) {
				result = map;
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
			hashMap.put(dir, bookNo);
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
	public BookInfo bookInfoFromFolder(File dir) {
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
	 * @param dir
	 * @return
	 */
	public BookInfo getBookInfo(File dir) {

		if (BookInfo.isBookInfoName(dir)) {
			log.warn("フォルダ名より、処理済みのフォルダと認識したため、そのまま情報を使用します。{}",
					dir.getAbsolutePath());
			return BookInfo.createBookInfo(dir);
		}
		BookInfo bookInfoFromFolder = bookInfoFromFolder(dir);
		if (bookInfoFromFolder != null) {
			return bookInfoFromFolder;
		}

		String barcode = getISBNFromFolderName(dir);
		if (barcode == null) {
			barcode = barcodeReader.autoReadDir(dir);
		}
		if (barcode != null) {

			BookInfo info = BookInfoFromWeb.getBookInfo(barcode);
			if (info != null) {
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

	public static List<String> booksNo(String no) {

		List<String> l = new ArrayList<String>();

		if (partermBetween.matcher(no).find()) {
			Matcher matcher = partermBetween.matcher(no);
			matcher.find();
			String s = matcher.group(1);
			String e = matcher.group(2);
			int s1 = Integer.parseInt(s);
			int e1 = Integer.parseInt(e);

			for (int i = s1; i <= e1; i++) {

				l.add(no_XX(i));
			}

		} else if (partermAll.matcher(no).find()) {
			Matcher matcher = partermAll.matcher(no);
			matcher.find();

			String e = matcher.group(1);
			int e1 = Integer.parseInt(e);

			for (int i = 1; i <= e1; i++) {

				l.add(no_XX(i));
			}

		} else {
			throw new IllegalStateException("変");
		}
		return l;
	}

	/**
	 * ファイルをまとめ直し、リビルドする。
	 * @param baseDir
	 * @param map
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void createCominName(File baseDir,
			SortedMap<BookInfo, File> map) throws IOException,
			InterruptedException {

		//コミックの名称（巻を除く）ごとに分別
		SortedMap<String, SortedMap<BookInfo, File>> m = new TreeMap<String, SortedMap<BookInfo, File>>() {
			@Override
			public SortedMap<BookInfo, File> get(Object key) {
				if (!this.containsKey(key)) {
					TreeMap<BookInfo, File> treeMap = new TreeMap<BookInfo, File>();
					this.put((String) key, treeMap);
				}
				return super.get(key);
			}

		};

		for (BookInfo bookInfo : map.keySet()) {
			String baseInfo = bookInfo.getAuthor() + "_"
					+ bookInfo.getTitleStr();
			log.info("分類しています。{} >>  {}", baseInfo, bookInfo.getInfo());
			m.get(baseInfo).put(bookInfo, map.get(bookInfo));
		}

		File temp1 = FileOperationUtil.createTempDir(baseDir, "1ファイル");
		File tempMany = FileOperationUtil.createTempDir(baseDir, "完成");

		for (Entry<String, SortedMap<BookInfo, File>> e : m.entrySet()) {
			SortedMap<BookInfo, File> value = e.getValue();
			Set<BookInfo> keySet = value.keySet();
			List<BookInfo> list = new ArrayList<BookInfo>(keySet);

			log.warn("基礎名の分類結果です。{} : data数 {}", e.getKey(), keySet.size());
			int size = list.size();
			for (int i = 0; i < 10; i++) {

				if ((i + 1) * 10 < size) {
					List<BookInfo> subList = list.subList(i * 10, (i + 1) * 10);
					String folderName = createCominName(subList);
					File path = createPath(baseDir, folderName);
					path.delete();
					path.mkdir();
					for (BookInfo bookInfo : subList) {
						File file = value.get(bookInfo);
						file.renameTo(createPath(path, file));

					}
					WinRARWrapper.encode(path, path);
					path.renameTo(createPath(tempMany, path.getName()));
				} else {
					List<BookInfo> subList = list.subList(i * 10, size);
					File path;

					if (subList.size() == 1) {
						File file = value.get(subList.get(0));
						path = createPath(baseDir, file);
						file.renameTo(path);
					} else {
						String folderName = createCominName(subList);
						path = createPath(baseDir, folderName);
						path.delete();
						path.mkdir();
						for (BookInfo bookInfo : subList) {
							File file = value.get(bookInfo);
							file.renameTo(createPath(path, file));

						}
					}

					WinRARWrapper.encode(path, path);

					if (i == 0 && subList.size() == 1) {
						path.renameTo(createPath(temp1, path.getName()));
					} else {
						path.renameTo(createPath(tempMany, path.getName()));
					}

					break;

				}
			}

		}

	}

	/**
	 * 前提条件　baseInfoが揃っているものを入れること
	 * @param list
	 * @return
	 */
	public static String createCominName(List<BookInfo> list) {

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

	private static String createComicNoStr(List<BookInfo> list) {

		int size = list.size();
		SortedSet<String> set = new TreeSet<String>();
		SortedSet<String> setNG = new TreeSet<String>();
		int noCount = Integer.parseInt(list.get(0).getNo());
		int noCountLast = Integer.parseInt(list.get(list.size() - 1).getNo());

		for (int i = 0; i < size; i++) {
			String noStr = list.get(i).getNo();
			int no = Integer.parseInt(noStr);
			for (int increment = 0; increment < 5; increment++) {
				if (no == noCount) {
					set.add(no_XX(noCount));
					noCount++;
					break;
				} else {
					setNG.add(no_XX(noCount));
					noCount++;
				}
				if (noCount > noCountLast) {
					break;
				}
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

	private static Map<String, String> map = new HashMap<String, String>();
	static {
		map.put("ジャンプ・コミックス", "Wジャンプ");
		map.put("Monthly shonen magazine comics", "Mマガジン");
		map.put("ヤングジャンプ・コミックス", "Yジャンプ");
		map.put("単行本コミックス", "");
		map.put("", "");
		map.put("", "");
		map.put("", "");
	}

	private static void convertpPublisherName(BookInfo info) {

		String publisherName = info.getPublisherName();
		String s = map.get(publisherName);
		if (s != null) {
			info.setPublisherName(s);
		}

	}

}
