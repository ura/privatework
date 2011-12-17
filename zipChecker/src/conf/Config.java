package conf;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.CloseUtil;

public class Config {
	private static Logger log = LoggerFactory.getLogger(Config.class);

	public static Config loadConfig(String file) {
		Properties p = new Properties();
		InputStream is = null;
		try {
			is = new BufferedInputStream(Config.class.getResourceAsStream(file));
			p.load(is);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			CloseUtil.close(is);
		}

		Config c = new Config();
		c.prop = p;

		return c;

	}

	private Properties prop;

	public Collection<String[]> getMoveSetting() {
		List<String[]> list = new ArrayList<String[]>();

		//TODO 設定周りのあり方は考える
		for (Map.Entry<Object, Object> e : prop.entrySet()) {
			String s = (String) e.getValue();

			String[] strings = s.split(",");
			list.add(strings);

		}
		return list;
	}

}
