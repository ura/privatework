package dir;

import java.io.File;

import junit.framework.TestCase;
import util.file.Dir;
import util.file.DirCollector;
import util.file.FileWalker;

@Deprecated
public class CollectorRemoteTest extends TestCase {

	private static final String BASE = "C:\\x\\data\\dev\\ws\\ws5\\comicJava\\testdata";
	private static final String NG = "C:\\x\\data\\dev\\ws\\ws5\\comicJava\\testdata\\NG";

	private static final String REMOTE_BASE = "\\\\XI-PC\\down\\data";

	/**
	 * \\XI-PC\down
	 */
	private static final String REMOTE_BASE_TEST = "\\\\XI-PC\\down\\temp";

	public void _testHandle() {

		DirCollector dirs = new DirCollector();

		new FileWalker().walk(new File(REMOTE_BASE), dirs);

		for (Dir dir : dirs.dirSet.values()) {
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

		DirCollector test = new DirCollector();

		new FileWalker().walk(new File(REMOTE_BASE_TEST), test);

		for (Dir dir : test.dirSet.values()) {

			for (File s : dir.fileSet) {
				System.out.println("TEST: " + s);
				//Dir nearDir = ScoreUtil.createDefault().dir(
				//		dirs.dirSet.values(), s);
				//if (nearDir != null) {
				//	System.out.println("TEST: " + nearDir.dir.getName());
				//}
				System.out.println();

			}

		}

	}
}
