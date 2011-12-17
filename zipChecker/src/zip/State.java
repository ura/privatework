/**
 * 
 */
package zip;

import java.io.File;

public enum State {
	OK, UNZIP_ERROR, FEW_FILE, OTHER, ZIP_OPEN_ERROR, NON_ZIP;

	public File getDir(File root) {
		File f = new File(root.getPath() + "\\" + name());
		if (f.exists() == false) {
			f.mkdir();
		}
		return f;
	}
}