package socre.name;

import java.util.Collection;

import util.CollectionUtil;
import util.StringUtil;

/**
 * ファイルのコア的な要素のみ切り出す。英数字のみからなるパーツ、話数のパーツを無視する。
 *
 */
public class FileNameParseCoreOnly implements FileNameParser {

	/**
	 * コアの要素のみ切り出す。
	 * そのため、「数字・英語のみの要素」「話数の要素」
	 */
	private static String[] patterns = new String[] { "^[\\w-]*$", "第[0-9]*話" };

	@Override
	public Collection<String> parse(String file) {

		Collection<String> c = CollectionUtil.toSortSet(StringUtil.parse(file, 2));
		CollectionUtil.nameFilter(c, patterns, true);

		return c;
	}

}
