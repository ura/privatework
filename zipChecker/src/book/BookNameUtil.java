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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.UserInput;
import util.WinRARWrapper;
import util.file.filter.DirFilter;
import book.webapi.Amazon;
import book.webapi.BookInfo;
import book.webapi.Rakuten;
import conf.ConfConst;
import static util.file.FileNameUtil.createPath;
import static util.file.FileNameUtil.getExt;
import static util.file.FileNameUtil.getFileName;

public class BookNameUtil {

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

	private static Pattern FILE_NAME_REG2 = Pattern
	//.compile("([!A-Za-zＡ-Ｚａ-ｚ_0-9#-_\\s]{2,20}).*(\\.[A-Za-z0-9]+)");
			.compile("([\\u4E00-\\u9FBF\\u0020-\\u007Ea-zA-Z0-9０-９　 \\u3040-\\u309F\\u30A0-\\u30FF\\u30A0-\\u30FF\\u30A0-\\u30FF]{2,40}).*(\\.[a-zA-Z]+)");

	private static Pattern FILE_NAME_REG1 = Pattern
			.compile("([!A-Za-zＡ-Ｚａ-ｚ_0-9#-_\\s]{2,40}).*(\\.[A-Za-z0-9]+)");

	private static Pattern FILE_NAME_REG3 = Pattern
			.compile("[^!0-9]*([!0-9a-z]{2,40})(\\.[A-Za-z0-9]+)");

	private static List<Pattern> FILE_NAME_REG_LIST = new ArrayList<>();
	static {
		FILE_NAME_REG_LIST.add(FILE_NAME_REG1);
		FILE_NAME_REG_LIST.add(FILE_NAME_REG2);
		FILE_NAME_REG_LIST.add(FILE_NAME_REG3);

	}

	/**
	 * 日本語のみの名称に変換します。
	 * シンプルな名前に置換した名称を返します。
	 */
	public static String createSimpleName(File f) {

		return createSimpleName(f, FILE_NAME_REG1);

	}

	private static String createSimpleName(File f, Pattern reg) {

		//元が十分に短い場合は変更しない
		if (getFileName(f).length() < 3) {
			return getFileName(f);
		}

		Matcher matcher = reg.matcher(f.getName());

		try {
			if (matcher.find()) {
				String group = matcher.group(1) + matcher.group(2);
				return group;
			}
		} catch (Exception e) {
			log.error("想定外のエラー", e);
		}

		return "0." + getExt(f);
	}

	/**
	 * 日本語のみの名称に変換します。
	 * シンプルな名前に置換した名称を返します。
	 */
	public static Map<File, File> createSimpleName(Collection<File> f) {

		File sample = null;
		for (Pattern REG : FILE_NAME_REG_LIST) {
			Map<File, File> map = new HashMap<>();
			Set<File> set = new HashSet<>();
			for (File file : f) {

				sample = file;
				String createSimpleName = createSimpleName(file, REG);
				File dest = createPath(file.getParent(), createSimpleName);
				map.put(file, dest);
				set.add(dest);
			}

			if (f.size() == set.size()) {
				return map;
			}
		}

		log.error("他のファイルと重複しないファイル名を作成することができませんでした。{}",
				sample.getAbsolutePath());
		throw new IllegalStateException("他のファイルと重複しないファイル名を作成することができませんでした。"
				+ sample.getAbsolutePath());

	}

	public static String kan(File f) {

		return kan(f.getName());

	}

	public static String kan(String s) {

		try {
			return "第" + bookNo(s) + "巻";
		} catch (IllegalArgumentException e) {

			log.warn(e.getMessage());
			log.warn("巻数の取得できないファイルが存在しました。");
			log.warn("どのように対応しますか？対応する場合は、文言を入力してください");

			if (UserInput.isInput()) {

				return UserInput.getUserInput();

			} else {
				throw e;
			}

		}

	}

	public static Map<File, BookInfo> getAllbookInfoFromBarcode(File root) {
		File[] dirs = root.listFiles(new DirFilter());

		Map<File, BookInfo> m = new HashMap<>();

		try {
			ExecutorService ex = Executors
					.newFixedThreadPool(THREAD_GET_BOOKINFO);
			List<Callable<Map<File, BookInfo>>> l = new ArrayList<>();
			for (File dir : dirs) {

				l.add(new BookInfoFromBarcodeTask(dir));

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

	private static class BookInfoFromBarcodeTask implements
			Callable<Map<File, BookInfo>> {

		public BookInfoFromBarcodeTask(File dir) {
			super();

			this.dir = dir;
		}

		private File dir;

		@Override
		public Map<File, BookInfo> call() throws Exception {

			BookInfo bookNo = BookNameUtil.bookInfoFromBarcode(dir);

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
	public static String bookInfoFromFolderName(File dir) {
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

	/**
	 * バーコードスキャンをして、書籍情報を取得します。
	 * @param dir
	 * @return
	 */
	public static BookInfo bookInfoFromBarcode(File dir) {

		if (BookInfo.isBookInfoName(dir)) {
			log.warn("フォルダ名より、処理済みのフォルダと認識したため、そのまま情報を使用します。{}",
					dir.getAbsolutePath());
			return BookInfo.createBookInfo(dir);
		}

		String barcode = bookInfoFromFolderName(dir);
		if (barcode == null) {
			barcode = BarcodeReader4Book.autoReadDir(dir);
		}
		if (barcode != null) {
			BookInfo info1 = Amazon.getInfo(barcode);
			BookInfo info2 = Rakuten.getInfo(barcode);

			BookInfo info = marge(info1, info2);
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

	private static BookInfo marge(BookInfo b1, BookInfo b2) {

		if (b1 == null) {
			return b2;
		}
		if (b2 == null) {
			return b1;
		}

		if (b1.getInfo().equals(b2.getInfo())) {
			return b1;
		} else {
			log.warn("書籍情報に差異があります。AWS   {}", b1.getInfo());
			log.warn("書籍情報に差異があります。楽天  {}", b2.getInfo());

			BookInfo r = b1;

			if (b1.getTitle().length() > b2.getTitle().length()) {
				r.setTitleStr(b2.getTitleStr());
			} else {
				r.setTitleStr(b1.getTitleStr());
			}

			if (b1.getAuthor().length() > b2.getAuthor().length()) {
				r.setAuthor(b2.getAuthor());
			} else {
				r.setAuthor(b1.getAuthor());
			}

			if (b1.getPublisherName().length() > b2.getPublisherName().length()) {
				r.setPublisherName(b2.getPublisherName());
			} else {
				r.setPublisherName(b1.getPublisherName());
			}

			log.warn("書籍情報を更新しました。　　　  {}", r.getInfo());

			return r;

		}

	}

	public static String bookNo(String no) {

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
			String baseInfo = bookInfo.getBaseInfo();
			log.info("分類しています。{} >>  {}", baseInfo, bookInfo.getInfo());
			m.get(baseInfo).put(bookInfo, map.get(bookInfo));
		}

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
				} else {
					List<BookInfo> subList = list.subList(i * 10, size);
					String folderName = createCominName(subList);
					File path = createPath(baseDir, folderName);
					path.delete();
					path.mkdir();
					for (BookInfo bookInfo : subList) {
						File file = value.get(bookInfo);
						file.renameTo(createPath(path, file));

					}
					WinRARWrapper.encode(path, path);
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
