package util;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import webapi.BookInfo;
import webapi.Rakuten;
import barcode.BarcodeReader;

public class NameUtil {

	private static Logger log = LoggerFactory.getLogger(NameUtil.class);

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
			.compile("([A-Za-zＡ-Ｚａ-ｚ_\\.0-9#-_\\s]+\\.[A-Za-z0-9]+)");

	/**
	 * アルファベット、数値のみのシンプルな名前に置換した名称を返します。
	 */
	public static String createSimpleName(File f) {

		Matcher matcher = fileName.matcher(f.getName());

		if (matcher.find()) {
			String group = matcher.group(1);
			return group;
		}

		throw new IllegalArgumentException(f.getName());

	}

	public static String kan(File f) {

		return kan(f.getName());

	}

	public static boolean isMultiFile(File f) {

		String name = f.getName();

		if (partermBetween.matcher(name).find()) {
			return true;
		} else if (partermAll.matcher(name).find()) {
			return true;
		} else {
			return false;
		}

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

	/**
	 * バーコードスキャンをして、書籍情報を取得します。
	 * @param dir
	 * @return
	 */
	public static String bookInfoFromBarcode(File dir) {

		String barcode = BarcodeReader.autoReadDir(dir);
		BookInfo info = Rakuten.getInfo(barcode);
		return info.getInfo();

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

	public static String createCominName(String name, Collection<File> list) {

		SortedSet<BookInfo> author = Rakuten.getInfo(new Rakuten.TitleQuery(
				name));
		int size = author.size();
		String authorText;

		if (size == 1) {

			authorText = author.first().getInfo();
		} else {

			log.warn("複数の著者候補がありました。");
			log.warn("無視する場合は、ENTERを押してください");

			BookInfo selectOne = UserInput.selectOne(author);
			if (selectOne != null) {
				convertpPublisherName(selectOne);
				authorText = selectOne.getInfo();
				authorText = authorText.replace("/", "_");
			} else {
				authorText = " ";
			}

		}

		String s = "[一般コミック]" + authorText + name + " " + NameUtil.kan(list);

		return s.replace("/", "_");

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

	public static String kan(Collection<File> list) {
		SortedSet<String> set = new TreeSet<String>();

		for (File f : list) {

			try {
				if (isMultiFile(f)) {
					set.addAll(booksNo(f.getName()));
				} else {
					set.add(bookNo(f.getName()));
				}
			} catch (IllegalArgumentException e) {
				log.warn(e.getMessage());
				log.warn("巻数の取得できないファイルが存在しました。");
				log.warn("どのように対応しますか？無視する場合は、ENTERを押してください");
				log.warn("入力を行った場合は、それがファイルの接尾子になります。（01-15巻＋画集）");

				if (UserInput.isInput()) {

					return UserInput.getUserInput();

				} else {

				}
			}

		}

		String first = set.first();
		String last = set.last();

		return "第" + first + "-" + last + "巻";

	}

}
