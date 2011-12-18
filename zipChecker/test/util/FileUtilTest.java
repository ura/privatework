package util;

import java.io.File;

import junit.framework.TestCase;
import util.file.FileOperationUtil;
import util.file.FileUtilExt;
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
			File f = FileOperationUtil.createFileName(string, dir);
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

	public void testConvertArc() {

		String[] args = { "BLOODY MONDAY " };
		// FileUtilExt.rebuildArc("L:\\DATA\\COMIC", "(��ʃR�~�b�N) ���l�Y2030",
		// "���l�Y2030 ");

		for (String s : args) {
			UserInput.getUserInputsSwing("�p�X����", 1);
			if (!s.trim().equals("")) {
				FileUtilExt.rebuildArc("L:\\DATA\\COMIC", "�L���O�_��",
						"�L���O�_��", "Kingdom");
			}
		}

		// FileUtilExt.rebuildArc("L:\\DATA\\COMIC",
		// "(��ʃR�~�b�N) CLAYMORE -�N���C���A-  ", "CLAYMORE -�N���C���A-");

	}

}
