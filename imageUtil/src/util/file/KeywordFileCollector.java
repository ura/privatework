package util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ディレクトリ、ファイルを収集して、溜め込む。
 *

 *
 */
public class KeywordFileCollector implements FileHandler {

	protected List<File> list = new ArrayList<File>();

	protected String[] keywords;

	public KeywordFileCollector(String... key) {
		this.keywords = key;

	}

	public boolean isHamdleFile(File f) {

		return true;
	}

	public boolean handle(File f)

	{

		if (f.isDirectory()) {

		} else {

			if (judge(f)) {
				list.add(f);
			}

		}

		return true;
	}

	protected boolean judge(File f) {

		for (String k : this.keywords) {
			if (f.getName().contains(k)) {
				return true;
			}

		}
		return false;

	}

	/**
	 * 収集したファイルを返す。
	 *
	 * @return
	 */
	public List<File> getFiles() {

		return list;

	}

}
