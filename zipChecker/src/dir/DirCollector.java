package dir;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.file.FileHandler;

/**
 * ディレクトリ、ファイルを収集して、溜め込む。
 * 情報は、Dirに溜め込む。
 *

 *
 */
public class DirCollector implements FileHandler, Iterable<Dir> {

	private static Logger log = LoggerFactory.getLogger(DirCollector.class);

	public SortedMap<File, Dir> dirSet = new TreeMap<File, Dir>(
			new Comparator<File>() {

				@Override
				public int compare(File o1, File o2) {

					return -o1.getAbsolutePath()
							.compareTo(o2.getAbsolutePath());

				}

			});

	public boolean isHamdleFile(File f) {

		return true;
	}

	@Override
	public Iterator<Dir> iterator() {

		return dirSet.values().iterator();
	}

	public boolean handle(File f)

	{

		if (f.isDirectory()) {
			log.info(f.getAbsolutePath() + "\t MAPSIZE:" + dirSet.size() + "\t"
					+ dirSet.containsKey(f));

			dirSet.put(f, new Dir(f));
			log.info(f.getAbsolutePath() + "\t MAPSIZE:" + dirSet.size() + "\t"
					+ dirSet.containsKey(f));
			log.info(dirSet.get(f).dir.getAbsolutePath());

		} else {
			log.debug(f.getAbsolutePath());
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
