package util.file;

import java.io.File;
import java.io.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.StaticUtil;

public class FileWalker {
	static Logger log = LoggerFactory.getLogger(FileWalker.class);

	/**
	 * WALKしてない・・・・
	 * 1層で止まっている。
	 *
	 * @param root
	 * @param handlers
	 */
	public void walk(File root, final FileHandler... handlers) {

		while (!root.exists()) {
			StaticUtil.sleep(20l);
			System.out.println(".");
		}

		if (root.isDirectory()) {

			log.info(root.getPath());

			File[] files = root.listFiles(new FileFilter() {

				@Override
				/**
				 * ハンドラーが受け入れるものと、フォルダを返す。
				 */
				public boolean accept(File pathname) {

					if (pathname.isDirectory()) {
						return true;
					}

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
						handler.handle(f);
					}
				}
				if (f.isDirectory()) {
					walk(f, handlers);
				}
			}

		}

	}
}
