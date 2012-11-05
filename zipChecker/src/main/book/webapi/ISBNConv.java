package book.webapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ISBNConv {
	private static Logger log = LoggerFactory.getLogger(ISBNConv.class);

	public static String to13From10(String str) {

		try {
			String s12 = "978" + str.substring(0, 9);
			int check_digit = 0;
			for (int i = 0; i < 12; i++) {
				check_digit += Integer.parseInt(s12.substring(i, i + 1))
						* (i % 2 == 0 ? 1 : 3);
			}
			check_digit = 10 - (check_digit % 10);
			return s12 + (check_digit == 10 ? 0 : check_digit);
		} catch (NumberFormatException e) {
			return null;
		}

	}

	public static String to10From13(String str) {

		try {

			String s9 = str.substring(3, 12);

			int check_digit = 0;
			for (int i = 0; i < 9; i++) {
				check_digit += Integer.parseInt(s9.substring(i, i + 1))
						* (10 - i);
			}
			String c = "";

			check_digit = 11 - (check_digit % 11);
			if (check_digit == 11) {
				c = "0";
			} else if (check_digit == 10) {
				c = "X";
			} else {
				c = Integer.toString(check_digit);
			}

			return s9 + c;

		} catch (NumberFormatException
				| java.lang.StringIndexOutOfBoundsException e) {
			log.warn("不正なISBNが存在します。[{}]", str);
		}
		return "";

	}

	public static boolean check(String str) {
		try {
			return str.equals(to13From13(str));
		} catch (Exception e) {

			log.warn("不正なISBNが存在します。[{}]", str);
			return false;
		}

	}

	public static String to13From13(String str) {

		try {
			String s12 = str.substring(0, 12);
			int check_digit = 0;
			for (int i = 0; i < 12; i++) {
				check_digit += Integer.parseInt(s12.substring(i, i + 1))
						* (i % 2 == 0 ? 1 : 3);
			}
			check_digit = 10 - (check_digit % 10);

			return s12 + (check_digit == 10 ? 0 : check_digit);
		} catch (NumberFormatException
				| java.lang.StringIndexOutOfBoundsException e) {
			log.warn("不正なISBNが存在します。[{}]", str);
		}
		return "";

	}
}
