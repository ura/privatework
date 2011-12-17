package util.file;

import java.io.File;


public class DirHandler implements FileHandler {

	public boolean isHamdleFile(File f) {
		if (f.isDirectory()) {
			return true;
		} else {
			return false;
		}
	}

	public boolean handle(File f, FileHandler... handlers)

	{
		new FileWalker().walk(f, handlers);
		return true;
	}

}
