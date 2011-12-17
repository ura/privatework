package socre.calculator;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import socre.name.FileNameParser;
import util.CollectionUtil;
import util.StringUtil;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import dir.Dir;

/**
 * ���̃t�H���_�ɓ����Ă���t�@�C���̓������݂āA�X�R�A��o�^����B
 * ��F����t�H���_�Ɂu���E��Y�v�u.avi�v�Ƃ������t�@�C���΂�������Ă���ꍇ�A
 * ���̓����Ɉ�v����t�@�C���������]������B
 *
 * @author poti
 *
 */
public class ScoreCalculatorByDirFile implements ScoreCalculator {

	private static Logger log = LoggerFactory
			.getLogger(ScoreCalculatorByDirFile.class);

	private FileNameParser fileNameParser;

	@Inject
	public ScoreCalculatorByDirFile(
			@Named("normal") FileNameParser fileNameParser) {
		super();
		this.fileNameParser = fileNameParser;
	}

	/**
	 * ���W�b�N�F
	 * �t�H���_�Ɋ܂܂�Ă���t�@�C���̖��O�𕪉�����B
	 *
	 *
	 * @param dir
	 * @param filePart �t�@�C�����B�����ς݁B�������Ă���K�v������̂��H�H�H
	 * @return
	 */
	public int score(Dir dir, String fileName) {

		Collection<String> set = fileNameParser.parse(FilenameUtils
				.getName(fileName));

		Map<String, CollectionUtil.Counter> map = null;
		int fCount = dir.fileNameSet.size();

		//�t�H���_�̏�Ԃ𕪐�
		for (String dirFile : dir.fileNameSet) {
			map = CollectionUtil.count(map, this.fileNameParser
					.parse(FilenameUtils.getName(fileName)), fCount);
		}

		int score = 0;
		if (map != null) {
			log.info("FILE FULL.{} ,Path.{} ", new Object[] { set,
					dir.dir.getPath() });

			for (Map.Entry<String, CollectionUtil.Counter> e : map.entrySet()) {
				//�t�H���_�̕��͌��ʂ������āA
				score = score + folder(e, set);
			}
		} else {
			log.debug("Map null:{}", dir.dir.getPath());
		}

		return score;
	}

	/**
	 * �t�H���_���̏�Ԃ�1�v�f�ƃt�H���_���̗v�f���X�R�A���B
	 * @param e
	 * @param set
	 * @return
	 */
	private int folder(Map.Entry<String, CollectionUtil.Counter> e,
			Collection<String> set) {
		for (String filePart : set) {
			if (StringUtil.contain(e.getKey(), filePart)) {
				//�t�H���_���Łu�H�v%�̐��̗͂v�f����������A�v���X�B
				//����ȉ��́A�m�C�Y�Ƃ��đ�����B
				if (e.getValue().per() > 30) {
					int score = e.getValue().per();
					log.info("FILE.{} ,DIR.{},Per.{},Count.{}", new Object[] {
							filePart, e.getKey(), e.getValue().per(),
							e.getValue().count });

					return score;
				}
			}
		}

		//�t�ɁA���̃t�H���_�̓����I�ȗv�f�ł���ɂ�������炸�A
		//�݂���Ȃ�������A�}�C�i�X�B
		//�ԑg���Ƃ����Y��
		int per = e.getValue().per();
		if (per > 70) {
			log.info("OUT!!! DIR.{},Per.{},Count.{}", new Object[] {
					e.getKey(), e.getValue().per(), e.getValue().count });
			//70*70 =4900 /40 = 100
			//80*80 =6400 /40 = 160
			//90*90 =8100 /40 = 200
			return -(per * per) / 40;
		} else {
			return 0;
		}

	}
}
