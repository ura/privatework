package conf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListConfig extends Config {
	private static Logger log = LoggerFactory.getLogger(ListConfig.class);

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
