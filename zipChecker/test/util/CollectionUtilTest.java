package util;

import java.util.Set;

import junit.framework.TestCase;

public class CollectionUtilTest extends TestCase {

	public void testNameFilter() {
		String name = "[アニメ] 電脳コイル 第01話 「メガネの子供たち」 (NHK-E DivX661 1280x720 Rev.3).avi";
		Set<String> set = CollectionUtil.toSortSet(StringUtil.parse(name));

		CollectionUtil.nameVideoFilter(set);

		for (String string : set) {
			System.out.println(string);
		}

	}

}
