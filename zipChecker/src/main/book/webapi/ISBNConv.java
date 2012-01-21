package book.webapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ISBNConv {
	private static Logger log = LoggerFactory.getLogger(ISBNConv.class);

	public static String to13(String str) {

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
