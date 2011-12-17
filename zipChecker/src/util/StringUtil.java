package util;

import java.util.regex.Pattern;

/**
 * 文字列操作系のユーティル
 * @author poti
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

}
