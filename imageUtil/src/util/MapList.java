package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapList<K, V> {
	private static Logger log = LoggerFactory.getLogger(MapList.class);

	private Map<K, List<V>> map = new HashMap<K, List<V>>() {

		private static final long serialVersionUID = 1L;

		@Override
		public List<V> put(K key, List<V> value) {
			return super.put(key, value);
		}

		@Override
		public List<V> get(Object key) {
			List<V> list = super.get(key);
			if (list != null) {
				return list;
			} else {
				return new ArrayList<V>();
			}
		}

	};

	public void add(K key, V value) {
		List<V> list = map.get(key);
		list.add(value);
		map.put(key, list);

	}

	/**
	 * 一つのキーに対して、複数のバリューがあるものだけ返します。
	 * @return
	 */
	public Collection<Map.Entry<K, List<V>>> duplicationEntrys() {
		List<Map.Entry<K, List<V>>> list = new ArrayList<Map.Entry<K, List<V>>>();

		for (Map.Entry<K, List<V>> e : map.entrySet()) {
			if (e.getValue().size() != 1) {
				for (V v : e.getValue()) {
					log.info("KEY 重複ファイル：{}:{}", e.getKey(), v);
				}
				log.info("");

				list.add(e);

			}
		}

		return list;
	}

}
