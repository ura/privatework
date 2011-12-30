package util;

import java.util.regex.Pattern;

/**
 * 文字列操作系のユーティル

 *
 */
public class StringUtil {

	private static Pattern parterm = Pattern
			.compile("[\\[\\]\\(\\)\\.\\【\\】 ・]");

	public static String[] parse(String s) {
		return _parse(s);
	}

	private static String[] _parse(String s) {
		return parterm.split(s);
	}

	/**
	 * 区切りとみなす可能性がある文字でパースする
	 * @param s
	 * @param len
	 * @return
	 */
	public static String[] parse(String s, int len) {
		String[] temp = _parse(s);
		int count = 0;
		for (String s2 : temp) {
			if (s2.trim().length() >= len) {
				count++;
			}
		}
		String[] result = new String[count];
		int i = 0;
		for (String s2 : temp) {
			if (s2.trim().length() >= len) {
				result[i] = s2.trim();
				i++;
			}
		}

		return result;

	}

	public static boolean contain(String s1, String s2) {
		return s1.toUpperCase().indexOf(s2.toUpperCase()) != -1;
	}

	/**
	 * 日本語っぽい文字だけを残す
	 * @param c
	 * @return
	 */
	public static boolean isJapaneseChar(char c) {
		Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock.of(c);

		if (Character.UnicodeBlock.HIRAGANA.equals(unicodeBlock))
			return true;

		if (Character.UnicodeBlock.KATAKANA.equals(unicodeBlock))
			return true;

		if (Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				.equals(unicodeBlock))
			return true;

		if (Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(unicodeBlock))
			return true;

		if (Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				.equals(unicodeBlock))
			return true;
		if (Character.UnicodeBlock.BASIC_LATIN.equals(unicodeBlock))
			return true;

		return false;
	}

	public static String toJapaneseStr(String s) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char charAt = s.charAt(i);
			if (isJapaneseChar(charAt)) {
				sb.append(charAt);
			}

		}
		return sb.toString();

	}

}
