package dir;

import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import socre.name.FileNameParseCoreOnly;
import socre.name.FileNameParser;
import util.CollectionUtil;
import util.StringUtil;
import util.CollectionUtil.Counter;
import util.file.FileMoveUtil;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Dir {

	private static Logger log = LoggerFactory.getLogger(Dir.class);

	public Dir(File dir) {
		super();
		this.dir = dir;

		String path = dir.getPath();

		String[] args = path.split("\\\\");
		nameSet = new TreeSet<String>();
		fileNameSet = new TreeSet<String>();

		for (String s : args) {
			nameSet.add(s);
		}

	}

	public File dir;
	public SortedSet<String> nameSet;
	public SortedSet<String> fileNameSet;

	@Inject
	@Named("core")
	private FileNameParser fileNameParser;

	public void addFile(File f) {
		fileNameSet.add(f.getPath());
	}

	//TODO �����ɋ@�\������̂������̂��͌����B
	/**
	 * �f�B���N�g���̒����m�F���A�t�@�C���̗ގ����������A�t�H���_�쐬�A����сAMOVE���s���B
	 *
	 */
	public void createNewDir() {
		//TODO �p�[�T�̓���ւ�
		FileNameParser fileNameParser = new FileNameParseCoreOnly();

		Map<String, CollectionUtil.Counter> map = null;

		//�t�H���_�̏�Ԃ𕪐�
		for (String dirFile : this.fileNameSet) {
			map = CollectionUtil.count(map, fileNameParser.parse(FilenameUtils
					.getName(dirFile)), fileNameSet.size());
		}
		if (map != null) {
			SortedSet<MoveFiles> sort = new TreeSet<MoveFiles>();
			for (Entry<String, Counter> e : map.entrySet()) {
				//���̃t�H���_������ƂȂ�v�f�ł͂Ȃ��A
				//����萔�̂ӂ����邪��������
				if (e.getValue().per() < 50 && e.getValue().count > 6) {

					//�e�̃p�X�Œ�`���Ă��閼�̂ł̓t�H���_�����Ȃ��B
					if (!nameSet.contains(e.getKey())) {
						sort.add(new MoveFiles(e.getKey(), e.getValue().count));
					}
				}
			}
			//�Ƃ肠�����A�ӂ�킯
			SortedSet<String> temp = new TreeSet<String>(this.fileNameSet);
			for (MoveFiles key : sort) {
				key.registFile(temp);
			}
			for (MoveFiles key : sort) {

				if (key.srcFilePathSet.size() > 6) {
					log.info("newDir count{} :Folder {}:FILES {}:",
							new Object[] { key.count, key.getNewDir(),
									key.srcFilePathSet.size() });
					key.move();

				} else {
					log.info("no Dir count{} :Folder {}:FILES {}:",
							new Object[] { key.count, key.getNewDir(),
									key.srcFilePathSet.size() });

				}
			}

		}
	}

	/**
	 * �ړ��������̊Ǘ��p�̃N���X
	 * @author poti
	 *
	 */
	private class MoveFiles implements Comparable<Dir.MoveFiles> {
		public MoveFiles(String keyWord, int count) {
			super();
			this.keyWord = keyWord;
			this.count = count;
		}

		public String keyWord;
		public int count;
		public SortedSet<String> srcFilePathSet = new TreeSet<String>();

		public String getNewDir() {
			return dir.getPath() + "\\" + keyWord;
		}

		public void registFile(Collection<String> c) {

			for (Iterator<String> ite = c.iterator(); ite.hasNext();) {
				String filePath = ite.next();
				if (StringUtil
						.contain(FilenameUtils.getName(filePath), keyWord)) {
					srcFilePathSet.add(filePath);
					ite.remove();
				}
			}
		}

		public void move() {
			for (String file : srcFilePathSet) {
				FileMoveUtil.move(new File(file), getNewDir());
			}

		}

		@Override
		public int compareTo(MoveFiles o) {

			return o.count - this.count;
		}

	}

	@Override
	public String toString() {

		return dir.getPath() + ": file=" + fileNameSet.size();
	}

}
