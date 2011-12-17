package util;

import java.util.Set;

public class DebugUtil {

	public static String toString(Set<String> sSet) {
		StringBuilder sb = new StringBuilder();

		for (String s : sSet) {
			sb.append(s).append(",");
		}
		return sb.toString();

	}

	public static String toString(String[] sArray) {
		StringBuilder sb = new StringBuilder();

		for (String s : sArray) {
			sb.append(s).append(",");
		}
		return sb.toString();

	}

	public static String getClassName(Object o) {
		String clsNameWithPkg = o.getClass().getName();

		return clsNameWithPkg.substring(clsNameWithPkg.lastIndexOf(".") + 1,
				clsNameWithPkg.length());
	}
}
