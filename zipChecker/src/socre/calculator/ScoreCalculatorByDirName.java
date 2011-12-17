package socre.calculator;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import socre.name.FileNameParser;
import util.StringUtil;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import dir.Dir;

/**
 *
 */
public class ScoreCalculatorByDirName implements ScoreCalculator {

	private static Logger log = LoggerFactory
			.getLogger(ScoreCalculatorByDirName.class);

	private FileNameParser fileNameParser;

	@Inject
	public ScoreCalculatorByDirName(
			@Named("normal") FileNameParser fileNameParser) {
		super();
		this.fileNameParser = fileNameParser;
	}

	/**
	 * ���W�b�N�F
	 * �u�t�H���_�̃p�X�v�Ɓu�t�@�C�����v���r�A��������B
	 *
	 *
	 * @param dir
	 * @param filePart �t�@�C�����B�����ς݁B�������Ă���K�v������̂��H�H�H
	 * @return
	 */
	public int score(Dir dir, String fileName) {

		Collection<String> set = fileNameParser.parse(FilenameUtils
				.getName(fileName));

		int score = 0;
		for (String filePart : set) {
			//�t�@�C�������t�H���_���i�p�X�j���܂�ł�����A�A�A
			//���g�̃t�H���_�������ړ����Ă���K�v�͂Ȃ��B
			// [data][avi][���E��Y]
			//�Ƃ����t�H���_�\���Ȃ�A

			Collection<String> dirPartSet = new HashSet<String>();

			//�e�t�H���_���𕪉����ďW�ρB
			//�t�H���_�����Ƃɓ��l�̖��O���܂܂�Ă����΂����ɑ΂���Ή�
			// *****�A�j��\\****�A�j��\\******�A�j��
			//�݂����ȁE�E�E�E
			for (String dirPart : dir.nameSet) {
				dirPartSet.addAll(fileNameParser.parse(dirPart));
			}

			for (String dirPart : dirPartSet) {
				if (StringUtil.contain(dirPart, filePart)) {
					score = score + 100;
					break;
				}
			}

		}

		return score;
	}

}
