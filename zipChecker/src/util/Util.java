package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Util {

	private static Logger log = LoggerFactory.getLogger(Util.class);

	public static void sleep(long l) {

		try {
			Thread.sleep(l);
		} catch (InterruptedException e) {
			log.error("SLEEP ERROR", e);
		}

	}

	public static boolean easyRandom(int percent) {

		long time = System.nanoTime();


		if ((time % 100) < percent) {
			return true;
		} else {
			return false;
		}

	}
	public static boolean easyRandom(float percent) {

		long time = System.nanoTime();


		if ((time % 10000) < (percent*100)) {
			return true;
		} else {
			return false;
		}

	}
}
