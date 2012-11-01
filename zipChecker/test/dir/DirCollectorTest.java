package dir;

import java.io.File;

import junit.framework.TestCase;
import util.file.Dir;
import util.file.DirCollector;
import util.file.FileWalker;

public class DirCollectorTest extends TestCase {

	private static final String BASE = "C:\\x\\data\\dev\\ws\\ws5\\comicJava\\testdata";
	private static final String NG = "C:\\x\\data\\dev\\ws\\ws5\\comicJava\\testdata\\NG";

	public void testHandle() {

		DirCollector d = new DirCollector();

		new FileWalker().walk(new File(BASE), d);

		for (Dir dir : d.dirSet.values()) {
			StringBuilder sb = new StringBuilder();

			for (String s : dir.nameSet) {
				sb.append(s).append(",");
			}
			System.out.println();
			System.out.println("DIR:" + sb);

			for (File s : dir.fileSet) {
				System.out.println(s);
			}

		}

	}

}
