package util.file;

import java.io.File;
import java.util.*;

/**
 * ディレクトリ、ファイルを収集して、溜め込む。
 *

 *
 */
public class KeywordFileCollectorWithExculde extends KeywordFileCollector {

	protected Collection<String> exclude;

	public KeywordFileCollectorWithExculde(Collection<String> kewords,
			Collection<String> exclude) {

		super(kewords.toArray(new String[0]));
		this.exclude = exclude;

	}

	protected boolean judge(File f) {

		if (super.judge(f)) {

			for (String e : exclude) {
				boolean contains = f.getAbsolutePath().contains(e);

				if (contains) {
					return false;
				}

			}
			return true;
		}

		return false;

	}

}
