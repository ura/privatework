package util;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

import util.file.filter.DirFilter;
import webapi.Amazon;
import webapi.BookInfo;
import webapi.Rakuten;
import barcode.BarcodeReader;
import conf.ConfConst;
import static util.file.FileNameUtil.createPath;

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

	private static Pattern partermAll = Pattern.compile("全.*([0-9]+)");

	/**
	 * 日付を考慮した正規表現　日付は [] で囲まれている場合多し。
	 */
	private static Pattern partermBetween = Pattern
			.compile("[^\\[0-9]([0-9]{1,2})[-]+([0-9]{2})[^\\]]+");

	private static Pattern fileName = Pattern
			.compile("([A-Za-zＡ-Ｚａ-ｚ_\\.0-9#-_\\s]+)[^A-Za-zＡ-Ｚａ-ｚ_\\.0-9#-_\\s]*(\\.[A-Za-z0-9]+)");

	/**
	 * アルファベット、数値のみのシンプルな名前に置換した名称を返します。
	 */
	public static String createSimpleName(File f) {

		Matcher matcher = fileName.matcher(f.getName());

		if (matcher.find()) {
			String group = matcher.group(1) + matcher.group(2);
			return group;
		}

		throw new IllegalArgumentException(f.getName());

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

	static class BookInfoFromBarcodeTask implements
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
	 * バーコードスキャンをして、書籍情報を取得します。
	 * @param dir
	 * @return
	 */
	public static BookInfo bookInfoFromBarcode(File dir) {

		String barcode = BarcodeReader.autoReadDir(dir);
		if (barcode != null) {
			BookInfo info = Amazon.getInfo(barcode);
			if (info != null) {
				return info;
			} else {

				info = Rakuten.getInfo(barcode);
				if (info != null) {
					return info;
				}

				log.warn("ISBNより書籍情報が取得出来なかったので、フォルダ名を返します。{}", dir);

				return new BookInfo(dir.getName());
			}

		} else {

			log.warn("バーコード情報が取得出来なかったので、フォルダ名を返します。{}", dir);

			return new BookInfo(dir.getName());
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

			log.info("基礎名の分類結果です。{} : data数 {}", e.getKey(), keySet.size());
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
