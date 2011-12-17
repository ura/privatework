package util.file;

import java.io.File;
import java.io.FileFilter;



public class FileWalker {

	public void walk(File root, final FileHandler... handlers) {

		if (root.isDirectory()) {

			File[] files = root.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {

					for (FileHandler handler : handlers) {
						if (handler.isHamdleFile(pathname)) {
							return true;
						}
					}

					return false;
				}
			});

			for (File f : files) {
				for (FileHandler handler : handlers) {
					if (handler.isHamdleFile(f)) {
						handler.handle(f, handlers);
					}
				}
			}

		}

	}
}
