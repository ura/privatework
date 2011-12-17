package util;

import java.io.File;

import junit.framework.TestCase;
import util.file.FileUtilExt;
import util.file.FileMoveUtil;
import util.file.FileWalker;
import dir.DirCollector;

public class FileUtilTest extends TestCase {
	private static final String BASE = "";

	private static final String REMOTE_TOP = "";
	private static final String REMOTE_BASE = "";
	private static final String REMOTE_BASE_TEST = "";

	public void _testDeleteSameFile() {

		FileUtilExt.deleteSameFile(REMOTE_TOP);

	}

	public void _testCreateDir() {

		FileUtilExt.createDir(REMOTE_TOP);

	}

	public void _testCreateFileName() {
		File dir = new File(BASE);
		String[] files = dir.list();

		for (String string : files) {
			File f = FileMoveUtil.createFileName(string, dir);
			System.out.println(f.getName());
		}

	}

	public void _testClassifyAll() {

		DirCollector dirs = new DirCollector();
		new FileWalker().walk(new File(REMOTE_BASE), dirs);

		DirCollector test = new DirCollector();
		new FileWalker().walk(new File(REMOTE_BASE_TEST), test);

		FileUtilExt
				.classifyAll(dirs.dirSet.values(), test.getAllFileFullPath());
	}

	public void _testConvertArc() {

		FileUtilExt
				.convertArc("L:\\DATA\\COMIC\\(一般コミック) [高橋留美子] めぞん一刻 ワイド版 全10巻.rar");

	}

	public void testConvertArc() {

		String[] args = { "BLOODY MONDAY " };
		// FileUtilExt.rebuildArc("L:\\DATA\\COMIC", "(一般コミック) 狂四郎2030",
		// "狂四郎2030 ");

		for (String s : args) {
			UserInput.getUserInputsSwing("パスを入力",1);
			if (!s.trim().equals("")) {
				FileUtilExt.rebuildArc("L:\\DATA\\COMIC","キングダム","キングダム","Kingdom");
			}
		}

		// FileUtilExt.rebuildArc("L:\\DATA\\COMIC",
		// "(一般コミック) CLAYMORE -クレイモア-  ", "CLAYMORE -クレイモア-");

	}

}
