package collection;

import junit.framework.TestCase;

public class MapListTest extends TestCase {


	public  void testhoge(){

		MapList<String,Integer> mapList = new MapList<String, Integer>();

		mapList.put("aaa", 1);
		mapList.put("aaa", 2);
		mapList.put("aaa", 3);
		mapList.put("aaa", 4);


	}
}
