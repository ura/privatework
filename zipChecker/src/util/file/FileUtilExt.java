package util.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import log.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import socre.ScoreUtil;
import util.CollectionUtil;
import util.MapList;
import util.NameUtil;
import util.UserInput;
import util.WinRARWrapper;
import zip.State;
import zip.ZipChecker;
import dir.Dir;
import dir.DirCollector;

public class FileUtilExt extends ObjectUtil {
	static Pattern fileNoPattern = Pattern.compile("(.*)_(\\d*)");
	static Logger log = LoggerFactory.getLogger(FileUtilExt.class);

	/**
	 * �p�X���[�h���̃t�@�C�����폜���܂��B
	 * 
	 */
	public static void movePassZipAll(String src) {

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(new File(src), srcDir);
		Collection<String> allFileFullPath = srcDir.getAllFileFullPath();

		File moveDir = new File(src + "\\" + "�S�~��");
		moveDir.mkdir();

		Checker checker = new Checker(new File(src));

		for (String string : allFileFullPath) {
			File f = new File(string);

			if (checker.check(f)) {
				log.info(Log.OP, "SKIP CHECK file:{}", string);
				continue;
			}
			log.info(Log.OP, "CHECK file:{}", string);

			switch (ZipChecker.check(f)) {
			case OK:
				checker.registration(f);
				break;
			case NON_ZIP:
				break;
			case ZIP_OPEN_ERROR:
				log.info(Log.OP, "Delete PASSWORD file:{}", string);
				FileMoveUtil.moveToDir(f, State.ZIP_OPEN_ERROR.getDir(moveDir));
				break;
			case FEW_FILE:
				log.info(Log.OP, "Delete PASSWORD file:{}", string);
				FileMoveUtil.moveToDir(f, State.FEW_FILE.getDir(moveDir));
				break;
			case UNZIP_ERROR:
				log.info(Log.OP, "Delete PASSWORD file:{}", string);
				FileMoveUtil.moveToDir(f, State.UNZIP_ERROR.getDir(moveDir));
				break;
			case OTHER:
				log.info(Log.OP, "Delete PASSWORD file:{}", string);
				FileMoveUtil.moveToDir(f, State.OTHER.getDir(moveDir));

				break;

			default:
				log.error(Log.OP, "�s����switch file:{}", string);

				break;

			}

		}

		checker.save();
	}

	private static final String WORK_DIR = "N:\\tmp";

	/**
	 * ���k�t�@�C���̌`����ς��܂��B rar��zip�ɁB ����ɁA����q���k�̏ꍇ�A����q�̓W�J���s���B
	 * 
	 */
	public static void convertArc(String src) {

		File srcFile = new File(src);

		try {
			String work = FileMoveUtil.createTempDir(WORK_DIR);
			WinRARWrapper.decode(src, work);

			File dir = new File(work);

			// �𓀌�̃t�H���_���ŁA�[���Ƃ���ɃA�[�J�C�u������ꍇ�A
			// �����Ɏ����Ă��āA����݂̂����X�g����
			FileMoveUtil.moveParent(dir, "zip", "rar");
			File[] list = FileMoveUtil.listFiles(dir, ".rar", ".zip");

			for (File zipFile : list) {

				String childDir = work + "/" + NameUtil.kan(zipFile);
				File cDir = new File(childDir);

				// �𓀂��āA�t�H���_���̃t�@�C����S����ɏグ��B
				WinRARWrapper.decode(zipFile, cDir);
				FileMoveUtil.moveParent(cDir, true);
				zipFile.delete();

			}

			WinRARWrapper.encode(work, WORK_DIR + "/"
					+ srcFile.getName().replace("rar", "zip"));

		} catch (IOException e) {
			log.error("�𓀎��ɑz��O�G���[", e);
		} catch (InterruptedException e) {
			log.error("�𓀎��ɑz��O�G���[", e);
		} catch (Exception e) {
			log.error("�𓀎��ɑz��O�G���[", e);
		}

	}

	/**
	 * ���k�t�@�C���̓��p�����s���܂��B 1���A2���A3���ƕ�����Ă���t�@�C�����������A��̃t�@�C���ɂ��܂��B
	 * 
	 * 
	 */
	public static void rebuildArc(String base, String name, String... keword) {

		KeywordCollector coll = new KeywordCollector(keword);
		new FileWalker().walk(new File(base), coll);

		try {

			String work = FileMoveUtil.createTempDir(WORK_DIR);
			File workF = new File(work);
			List<File> files = coll.getFiles();

			Collection<File> newList = UserInput.selectManySwing(files);
			for (File file : newList) {
				log.info("rebuildArc target {}", file.toString());
			}

			for (File zipFile : newList) {

				if (NameUtil.isMultiFile(zipFile)) {
					WinRARWrapper.decode(zipFile, workF);
					FileMoveUtil.moveParent(workF,  "zip", "rar");
					File[] childList = FileMoveUtil.listFiles(workF, ".rar",
							".zip");

					for (File z : childList) {

						String childDir = work + "/" + NameUtil.kan(z);
						File cDir = new File(childDir);

						// �𓀂��āA�t�H���_���̃t�@�C����S����ɏグ��B
						WinRARWrapper.decode(z, cDir);
						FileMoveUtil.moveParent(cDir, true);

					}

				} else {
					String childDir = work + "/" + NameUtil.kan(zipFile);
					File cDir = new File(childDir);

					// �𓀂��āA�t�H���_���̃t�@�C����S����ɏグ��B
					WinRARWrapper.decode(zipFile, cDir);
					FileMoveUtil.moveParent(cDir, true);

				}

				// zipFile.delete();

			}

			FileMoveUtil.deleteEmptyDir(workF);

			WinRARWrapper.encode(work, WORK_DIR + "/"
					+ NameUtil.createCominName(name, files));
			for (File zipFile : files) {
				FileMoveUtil.move(zipFile, "L:\\tmp");
			}

		} catch (IOException e) {
			log.error("�𓀎��ɑz��O�G���[", e);
		} catch (InterruptedException e) {
			log.error("�𓀎��ɑz��O�G���[", e);
		} catch (Exception e) {
			log.error("�𓀎��ɑz��O�G���[", e);
		}

	}

	/**
	 * �����t�@�C���������Ă���f�B���N�g���������A �t�H���_��V�K�ɍ쐬�A�ړ����s���܂��B
	 * 
	 * @param src
	 */
	public static void createDir(String src) {

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(new File(src), srcDir);

		for (Dir dir : srcDir.dirSet.values()) {
			dir.createNewDir();

		}
	}

	/**
	 * �����t�@�C�����폜���܂��B
	 */
	public static void deleteSameFile(String src) {

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(new File(src), srcDir);
		Collection<String> allFileFullPath = srcDir.getAllFileFullPath();

		MapList<Long, File> map = new MapList<Long, File>();

		List<File> list = new ArrayList<File>();
		for (String filePath : allFileFullPath) {
			File f = new File(filePath);
			list.add(f);
		}

		deleteFile(list);

	}

	public static void deleteSameFile(List<File> list) {

		MapList<Long, File> map = new MapList<Long, File>();

		for (File f : list) {

			long l = f.length();
			map.add(l, f);
		}
		for (Map.Entry<Long, List<File>> e : map.duplicationEntrys()) {
			// TODO �傫���t�@�C���́ACRC���d�����Ǝv���Ă������A�Ƃ肠�����A����Ă݂���j��
			// 1000M�ȉ���������
			if (e.getKey().longValue() < 5000 * 1000 * 1000l) {
				deleteSamaFileByCRC(e.getValue());
			} else {

			}
		}
	}

	private static void deleteSamaFileByCRC(List<File> list) {
		MapList<Long, File> mapList = new MapList<Long, File>();

		if (list.size() == 1) {
			log.info("���̃t�@�C���͏d���̉\�����Ȃ����߁A�X�L�b�v���܂��B FILE {}", list.get(0));
			return;
		}

		for (File f : list) {
			long crc;
			try {
				crc = FileUtils.checksumCRC32(f);

				log.info("CRC [{}] FILE {}", crc, f.getName());
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
			mapList.add(crc, f);
		}

		for (Entry<Long, List<File>> e : mapList.duplicationEntrys()) {
			List<File> value = e.getValue();

			if (log.isInfoEnabled()) {
				for (File file : value) {
					log.info("�d��CRC [{}] FILE {}", e.getKey(), file.getName());
				}
			}
			List<File> deleteFile = deleteFile(value);

			list.removeAll(deleteFile);

		}

	}

	/**
	 * �ЂƂ̃t�@�C�����c���č폜���܂��B
	 * 
	 * @param list
	 */
	private static List<File> deleteFile(List<File> list) {
		Collections.sort(list, new NameFileComparator());
		List<File> delList = new ArrayList<File>();

		for (int i = 1; i < list.size(); i++) {
			log.info(Log.OP, "DELETE FILE {}", list.get(i));
			list.get(i).delete();
			delList.add(list.get(i));

		}

		return delList;
	}

	/**
	 * �t�@�C�����̃t�B���^���A���ށB
	 * 
	 * @param src
	 * @param nameFilter
	 * @param dest
	 */
	public static void classifyAll(String src, String[] nameFilter, String dest) {

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(new File(src), srcDir);

		Collection<String> allFileFullPath = srcDir.getAllFileFullPath();
		CollectionUtil.nameFilter(allFileFullPath, nameFilter, false);

		DirCollector destDir = new DirCollector();
		new FileWalker().walk(new File(dest), destDir);

		FileUtilExt.classifyAll(destDir.dirSet.values(), allFileFullPath);

	}

	/**
	 * �t�@�C���𕪗ނ��܂��B�����̃t�@�C���𓯎��ɑΏۂɂ��܂��B
	 * 
	 * @param dirs
	 * @param fileNames
	 */
	public static void classifyAll(Collection<Dir> dirs,
			Collection<String> filepaths) {
		for (String filePath : filepaths) {
			classify(dirs, filePath, ScoreUtil.createDefault());
			if (log.isInfoEnabled()) {
				log.info("");

			}
		}

	}

	/**
	 * �t�@�C���𕪗ނ��܂��B
	 * 
	 * @param dirs
	 * @param fileName
	 * @return
	 */
	public static boolean classify(Collection<Dir> dirs, String fileName) {
		return classify(dirs, fileName, ScoreUtil.createDefault());
	}

	/**
	 * �t�@�C���𕪗ނ��܂��B
	 * 
	 * @param dirs
	 * @param fileName
	 * @return
	 */
	public static boolean classify(Collection<Dir> dirs, String filePath,
			ScoreUtil util) {
		Dir nearDir = util.dir(dirs, filePath);
		if (log.isInfoEnabled()) {
			log.info("{} >> {}", new Object[] { filePath, nearDir });

		}
		if (nearDir != null) {
			FileMoveUtil.moveToDir(new File(filePath), nearDir.dir);
			return true;
		} else {
			return false;
		}
	}

}
