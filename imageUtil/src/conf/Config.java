package conf;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.CollectionUtil.Counter;
import util.CollectionUtil.MapCounter;

public class Config {

	private static Logger log = LoggerFactory.getLogger(ListConfig.class);

	private static MapCounter<String> counter = new MapCounter<>();

	public static <V extends Config> V loadConfig(String file, Class<V> cls) {
		Properties p = new Properties();

		try (InputStream is = new BufferedInputStream(
				ListConfig.class.getResourceAsStream(file));) {

			p.load(is);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
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

	public String[] getVals(String key) {
		return prop.getProperty(key).split(",");
	}

	public String getRandamVal(String key) {

		String[] split = prop.getProperty(key).split(",");
		return split[counter.increment(key) % split.length];

	}

	public int getInt(String key) {
		return Integer.parseInt(prop.getProperty(key));
	}

	public boolean getBoolean(String key) {
		return Boolean.valueOf((prop.getProperty(key)));
	}
}