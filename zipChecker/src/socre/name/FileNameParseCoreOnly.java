package socre.name;

import java.util.Collection;

import util.CollectionUtil;
import util.StringUtil;

/**
 * �t�@�C���̃R�A�I�ȗv�f�̂ݐ؂�o���B�p�����݂̂���Ȃ�p�[�c�A�b���̃p�[�c�𖳎�����B
 *
 */
public class FileNameParseCoreOnly implements FileNameParser {

	/**
	 * �R�A�̗v�f�̂ݐ؂�o���B
	 * ���̂��߁A�u�����E�p��݂̗̂v�f�v�u�b���̗v�f�v
	 */
	private static String[] patterns = new String[] { "^[\\w-]*$", "��[0-9]*�b" };

	@Override
	public Collection<String> parse(String file) {

		Collection<String> c = CollectionUtil.toSortSet(StringUtil.parse(file, 2));
		CollectionUtil.nameFilter(c, patterns, true);

		return c;
	}

}
