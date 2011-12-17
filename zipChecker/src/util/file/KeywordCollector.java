package util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ディレクトリ、ファイルを収集して、溜め込む。
 *
 * @author poti
 *
 */
public class KeywordCollector implements FileHandler {

	private List<File> list = new ArrayList<File>();

	private String[] keywords;

	public KeywordCollector(String... key) {
		this.keywords = key;

	}

	public boolean isHamdleFile(File f) {

		return true;
	}

	public boolean handle(File f, FileHandler... handlers)

	{

		if (f.isDirectory()) {

			new FileWalker().walk(f, handlers);
		} else {

			for (String k : keywords) {
				if (f.getName().contains(k)) {
					list.add(f);
				}
			}

		}

		return true;
	}

	/**
	 * 収集したファイルを返す。 なお、この時点で、重複ファイルの排除を行っておく。
	 *
	 * @return
	 */
	public List<File> getFiles() {

		FileUtilExt.deleteSameFile(list);
		return list;

	}

}
