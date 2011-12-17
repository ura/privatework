package conf;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.CloseUtil;

public class Config {

	private static Logger log = LoggerFactory.getLogger(ListConfig.class);

	public static <V extends Config> V loadConfig(String file, Class<V> cls) {
		Properties p = new Properties();
		InputStream is = null;
		try {
			is = new BufferedInputStream(
					ListConfig.class.getResourceAsStream(file));
			p.load(is);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			CloseUtil.close(is);
		}

		V c = null;
		try {
			c = cls.newInstance();
			c.prop = p;
		} catch (InstantiationException | IllegalAccessException e) {

			log.error("想定外", e);
		}

		return c;

	}

	protected Properties prop;

	public Config() {
		super();
	}

	public String getVal(String key) {
		return prop.getProperty(key);
	}

}