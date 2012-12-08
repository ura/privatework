package util.file.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * フォルダのみを拾い出すフィルター。
 * パス名に指定の文字を含んでいるかも確認できる。
 *
 */
public class DirFilter implements FileFilter {

	private String dirName;

	public DirFilter() {

	}

	public DirFilter(String dirName) {
		super();
		this.dirName = dirName;
	}

	@Override
	public boolean accept(File pathname) {

		boolean directory = pathname.isDirectory();

		if (directory) {
			if (dirName == null) {
				return true;
			} else if (pathname.getName().contains(dirName)) {
				return true;
			}

		}

		return false;
	}
}
