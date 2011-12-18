package dir;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.file.FileHandler;
import util.file.FileWalker;

/**
 * ディレクトリ、ファイルを収集して、溜め込む。
 * 情報は、Dirに溜め込む。
 *

 *
 */
public class DirCollector implements FileHandler {

	private static Logger log = LoggerFactory.getLogger(Dir.class);

	public SortedMap<File, Dir> dirSet = new TreeMap<File, Dir>(
			new Comparator<File>() {

				@Override
				public int compare(File o1, File o2) {

					return (int) (o2.length() - o1.length());

				}

			});

	public boolean isHamdleFile(File f) {

		return true;
	}

	public boolean handle(File f, FileHandler... handlers)

	{
		log.info(f.getAbsolutePath());

		if (f.isDirectory()) {

			dirSet.put(f, new Dir(f));
			new FileWalker().walk(f, handlers);

		} else {
			Dir d = dirSet.get(f.getParentFile());
			if (d == null) {
				d = new Dir(f.getParentFile());
				dirSet.put(f.getParentFile(), d);
			}
			d.addFile(f);
		}

		return true;
	}

	public Collection<String> getAllFileFullPath() {
		Collection<String> result = new ArrayList<String>();
		for (Dir dir : this.dirSet.values()) {

			for (String s : dir.fileNameSet) {
				result.add(s);
			}
		}
		return result;

	}
}
