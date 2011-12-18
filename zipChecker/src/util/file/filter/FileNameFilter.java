package util.file.filter;

import java.io.File;
import java.io.FileFilter;

/**
 * フォルダのみを拾い出すフィルター。
 * パス名に指定の文字を含んでいるかも確認できる。
 *
 */
public class FileNameFilter implements FileFilter {

	public enum MODE {

		INCLUDE {

			@Override
			public boolean accept(String[] n, File f) {
				boolean b = false;
				for (String string : n) {
					b = b || f.getName().contains(string);

				}
				return b;
			}

		},
		EXT_INCLUDE {

			@Override
			public boolean accept(String[] n, File f) {
				boolean b = false;
				for (String string : n) {
					b = b || f.getName().endsWith(string);

				}
				return b;

			}

		},

		EXCLUDE {

			@Override
			public boolean accept(String[] n, File f) {
				boolean b = true;
				for (String string : n) {
					b = b && !f.getName().contains(string);

				}
				return b;

			}

		},

		EXT_EXCLUDE {

			@Override
			public boolean accept(String[] n, File f) {
				boolean b = true;
				for (String string : n) {
					b = b && !f.getName().endsWith(string);

				}
				return b;
			}

		};

		public boolean accept(String[] n, File f) {
			//DUMMY
			return false;

		}

	};

	private String[] name;
	private MODE mode;

	public FileNameFilter(MODE mode, String... name) {
		super();
		this.name = name;
		this.mode = mode;
	}

	@Override
	public boolean accept(File pathname) {

		boolean directory = pathname.isDirectory();

		if (!directory) {
			return mode.accept(name, pathname);
		}

		return false;
	}
}
