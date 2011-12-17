package dir;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import util.file.FileHandler;
import util.file.FileWalker;


/**
 * �f�B���N�g���A�t�@�C�������W���āA���ߍ��ށB
 * ���́ADir�ɗ��ߍ��ށB
 *
 * @author poti
 *
 */
public class DirCollector implements FileHandler {

	public Map<File, Dir> dirSet = new HashMap<File, Dir>();

	public boolean isHamdleFile(File f) {

		return true;
	}

	public boolean handle(File f, FileHandler... handlers)

	{

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
