package util.file;

import java.io.File;

public interface FileHandler {

	boolean isHamdleFile(File f);

	boolean handle(File f, FileHandler... handlers);

}
