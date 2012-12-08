package util.file;

import java.io.File;
import java.util.Collection;
import java.util.Map;

public interface NameUtil {

	/**
	 * ファイル名置換用のマッピングをする。
	 * @param f
	 * @return ソース、変更先の名称案
	 */
	public Map<File, File> createSimpleName(Collection<File> f);
}
