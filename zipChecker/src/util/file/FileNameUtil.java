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
		String strs[] = str.split("\\.");

		return strs[strs.length - 1];
	}

	/**
	 * 拡張子を取得
	 */
	public static String getExt(File f) {

		return getExt(f.getName());
	}

	/**
	 * 拡張子を覗いたファイル名を取得する
	 * @param str
	 * @return
	 */
	public static String getFileName(String str) {
		String ext = getExt(str);

		return str.substring(0, (str.length() - ext.length() - 1));

	}

	/**
	 * 拡張子を覗いたファイル名を取得する
	 * @param str
	 * @return
	 */
	public static String getFileName(File f) {

		return getFileName(f.getName());

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
