package collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class MapList<K, V> {

	private Map<K, Collection<V>> innerMap = new HashMap<K, Collection<V>>();

	//private Class<? super Collection> listCls;

	public MapList(){

		//this.listCls=ArrayList.class;
	}

	public MapList(Class<Collection<V>> c){

	}

	public void put(K k, V v) {

		Collection<V> c = innerMap.get(k);

		if (c == null) {
			c=new ArrayList<V>();

			innerMap.put(k, c);
		}

		c.add(v);


	}

	public Set<K> keySet(){

		Set<Entry<K,Collection<V>>> set = innerMap.entrySet();

		return innerMap.keySet();
	}

	public Set<Entry<K,Collection<V>>>  entrySet(){
		return  innerMap.entrySet();
	}



	public Collection<V> get(K k) {
		return innerMap.get(k);
	}

	public boolean contains(K k) {
		return innerMap.containsKey(k);
	}

	public int size() {
		return innerMap.size();
	}



}
