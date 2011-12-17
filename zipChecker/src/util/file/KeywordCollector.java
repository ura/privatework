package util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * �f�B���N�g���A�t�@�C�������W���āA���ߍ��ށB
 *
 * @author poti
 *
 */
public class KeywordCollector implements FileHandler {

	private List<File> list = new ArrayList<File>();

	private String[] keywords;

	public KeywordCollector(String... key) {
		this.keywords = key;

	}

	public boolean isHamdleFile(File f) {

		return true;
	}

	public boolean handle(File f, FileHandler... handlers)

	{

		if (f.isDirectory()) {

			new FileWalker().walk(f, handlers);
		} else {

			for (String k : keywords) {
				if (f.getName().contains(k)) {
					list.add(f);
				}
			}

		}

		return true;
	}

	/**
	 * ���W�����t�@�C����Ԃ��B �Ȃ��A���̎��_�ŁA�d���t�@�C���̔r�����s���Ă����B
	 *
	 * @return
	 */
	public List<File> getFiles() {

		FileUtilExt.deleteSameFile(list);
		return list;

	}

}
