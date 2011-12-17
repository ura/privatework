package util;

import java.util.Set;

import junit.framework.TestCase;

public class CollectionUtilTest extends TestCase {

	public void testNameFilter() {
		String name = "[�A�j��] �d�]�R�C�� ��01�b �u���K�l�̎q�������v (NHK-E DivX661 1280x720 Rev.3).avi";
		Set<String> set = CollectionUtil.toSortSet(StringUtil.parse(name));

		CollectionUtil.nameVideoFilter(set);

		for (String string : set) {
			System.out.println(string);
		}

	}

}
