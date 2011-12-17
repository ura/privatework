package util;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloseUtil {
	private static Logger log = LoggerFactory.getLogger(CloseUtil.class);

	public static void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
	}
}
