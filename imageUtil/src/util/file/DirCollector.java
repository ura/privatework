package util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ディレクトリ、ファイルを収集して、溜め込む。
 * 情報は、Dirに溜め込む。
 *

 *
 */
public class DirCollector implements FileHandler, Iterable<Dir> {

	public static DirCollector create(String root) {

		return create(new File(root));
	}

	public static DirCollector create(File root) {
		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(root, srcDir);
		return srcDir;
	}

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
			log.debug(f.getAbsolutePath() + "\t MAPSIZE:" + dirSet.size()
					+ "\t" + dirSet.containsKey(f));

			dirSet.put(f, new Dir(f));
			log.debug(f.getAbsolutePath() + "\t MAPSIZE:" + dirSet.size()
					+ "\t" + dirSet.containsKey(f));
			log.debug(dirSet.get(f).dir.getAbsolutePath());

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

	public Collection<File> getAllFile() {
		Collection<File> result = new ArrayList<>();
		for (Dir dir : this.dirSet.values()) {

			for (File s : dir.fileSet) {
				result.add(s);
			}
		}
		return result;

	}

	public Collection<String> getAllFilePath() {
		Collection<String> result = new ArrayList<>();
		for (Dir dir : this.dirSet.values()) {

			for (File s : dir.fileSet) {
				result.add(s.getAbsolutePath());
			}
		}
		return result;

	}
}
