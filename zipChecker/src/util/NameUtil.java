package util;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import webapi.BookInfo;
import webapi.Rakuten;

public class NameUtil {

	private static Pattern parterm = Pattern.compile("��([0-9]+)��");
	private static Pattern partermEng = Pattern.compile("v([0-9]+)\\.");
	private static Pattern partermEng2 = Pattern.compile("v([0-9]+)_");
	private static Pattern partermSimple = Pattern.compile(" ([0-9]+)\\.");

	private static Pattern partermAll = Pattern.compile("�S.*([0-9]+)");

	/**
	 * ���t���l���������K�\���@���t�� [] �ň͂܂�Ă���ꍇ�����B
	 */
	private static Pattern partermBetween = Pattern
			.compile("[^\\[0-9]([0-9]{1,2})[-]+([0-9]{2})[^\\]]+");

	private static Pattern fileName = Pattern.compile("([A-Za-z�`-�y��-��_\\.0-9#-_\\s]+\\.[A-Za-z0-9]+)");



	public static String createName(File f) {

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
			return "��" + bookNo(s) + "��";
		} catch (IllegalArgumentException e) {

			System.out.println(e.getMessage());
			System.out.println("�����̎擾�ł��Ȃ��t�@�C�������݂��܂����B");
			System.out.println("�ǂ̂悤�ɑΉ����܂����H�Ή�����ꍇ�́A��������͂��Ă�������");

			if (UserInput.isInput()) {

				return UserInput.getUserInput();

			} else {
				throw e;
			}

		}

	}

	public static String bookNo(String no) {

		Matcher matcher = parterm.matcher(no);
		if (matcher.find()) {
			String group = matcher.group(1);
			return group;
		}

		Matcher matcher2 = partermEng.matcher(no);
		if (matcher2.find()) {
			String group = matcher2.group(1);

			return no_XX(group);
		}

		Matcher matcher3 = partermSimple.matcher(no);
		if (matcher3.find()) {
			String group = matcher3.group(1);

			return no_XX(group);
		}

		Matcher matcher4 = partermEng2.matcher(no);
		if (matcher4.find()) {
			String group = matcher4.group(1);

			return no_XX(group);
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
			throw new IllegalStateException("��");
		}
		return l;
	}

	public static String createCominName(String name, Collection<File> list) {

		SortedSet<BookInfo> author = Rakuten.getInfo(name);
		int size = author.size();
		String authorText;

		if (size == 1) {

			authorText = author.first().getInfo();
		} else {

			System.out.println("�����̒��Ҍ�₪����܂����B");
			System.out.println("��������ꍇ�́AENTER�������Ă�������");

			BookInfo selectOne = UserInput.selectOne(author);
			if (selectOne != null) {
				convertpPublisherName(selectOne);
				authorText = selectOne.getInfo();
				authorText = authorText.replace("/", "_");
			} else {
				authorText = " ";
			}

		}

		String s = "[��ʃR�~�b�N]" + authorText + name + " " + NameUtil.kan(list);

		return s.replace("/", "_");

	}

	private static Map<String, String> map = new HashMap<String, String>();
	static {
		map.put("�W�����v�E�R�~�b�N�X", "W�W�����v");
		map.put("Monthly shonen magazine comics", "M�}�K�W��");
		map.put("�����O�W�����v�E�R�~�b�N�X", "Y�W�����v");
		map.put("�P�s�{�R�~�b�N�X", "");
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
				System.out.println(e.getMessage());
				System.out.println("�����̎擾�ł��Ȃ��t�@�C�������݂��܂����B");
				System.out.println("�ǂ̂悤�ɑΉ����܂����H��������ꍇ�́AENTER�������Ă�������");
				System.out.println("���͂��s�����ꍇ�́A���ꂪ�t�@�C���̐ڔ��q�ɂȂ�܂��B�i01-15���{��W�j");

				if (UserInput.isInput()) {

					return UserInput.getUserInput();

				} else {

				}
			}

		}

		String first = set.first();
		String last = set.last();

		return "��" + first + "-" + last + "��";

	}

}
