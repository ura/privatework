package socre.name;

import java.util.Collection;

import util.CollectionUtil;
import util.StringUtil;

public class FileNameParseImpl implements FileNameParser {

	@Override
	public Collection<String> parse(String file) {

		return CollectionUtil.nameVideoFilter(CollectionUtil
				.toSortSet(StringUtil.parse(file, 2)));
	}

}
