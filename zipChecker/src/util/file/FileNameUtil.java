package util.file;

import java.io.File;

public class FileNameUtil {

	/**
	 * パス区切り文字
	 */
	public static final char _ = File.separatorChar;

	/**
	 * 拡張子を取得
	 */
	public static String getExt(String str) {

		if (!haveExt(str)) {
			return "";
		}

		String strs[] = str.split("\\.");

		return strs[strs.length - 1];

	}

	/**
	 * 拡張子を取得
	 */
	public static String getExt(File f) {

		return getExt(f.getName());
	}

	public static boolean haveExt(File s) {

		return haveExt(s.getName());
	}

	public static boolean haveExt(String s) {

		return s.contains(".");
	}

	/**
	 * 拡張子を覗いたファイル名を取得する
	 * @param str
	 * @return
	 */
	public static String getFileName(String str) {

		if (haveExt(str)) {
			return str;
		}

		String ext = getExt(str);

		return str.substring(0, (str.length() - ext.length() - 1));

	}

	/**
	 * 拡張子を覗いたファイル名を取得する
	 * @param str
	 * @return
	 */
	public static String getFileName(File f) {
		try {
			return getFileName(f.getName());
		} catch (StringIndexOutOfBoundsException e) {
			System.out.println(f.getAbsolutePath());
			e.printStackTrace();
			return f.getName();
		}

	}

	/**
	 * フォルダとファイル名を渡してパスを作成します。
	 * @param str
	 * @return
	 */
	public static String createPathStr(File dir, String name) {

		return dir.getAbsolutePath() + _ + name;

	}

	/**
	 * フォルダとファイル名を渡してパスを作成します。
	 * @param str
	 * @return
	 */
	public static File createPath(File dir, String name) {

		return new File(createPathStr(dir, name));

	}

	/**
	 * フォルダとファイル名を渡してパスを作成します。
	 * @param str
	 * @return
	 */
	public static File createPath(String dir, String name) {

		return createPath(new File(dir), name);

	}

	/**
	 * フォルダとファイル名を渡してパスを作成します。
	 * @param str
	 * @return
	 */
	public static File createPath(File dir, File name) {

		return new File(createPathStr(dir, name.getName()));

	}

	/**
	 * 重複しない新しいパスを作成します。
	 * @param dir
	 * @param name
	 * @return
	 */
	public static File createNewPath(File dir, File name) {

		File path = createPath(dir, name);

		int i = 0;
		while (path.exists()) {
			i++;
			path = createPath(dir, "CP" + i + "_" + name.getName());
		}

		return path;

	}

}
