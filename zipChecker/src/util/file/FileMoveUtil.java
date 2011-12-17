package util.file;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Matcher;

import log.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.NameUtil;
import util.Util;

import dir.DirCollector;

/**
 * �t�@�C���̈ړ��A�폜�A�f�B���N�g���쐬�A���l�[���ȂǂȂǂ�Util
 *
 * @author poti
 *
 */
public class FileMoveUtil {

	static Logger log = LoggerFactory.getLogger(FileMoveUtil.class);

	/**
	 * �t�@�C�����f�B���N�g���Ɉړ����܂��B �t�@�C�������d�����Ă���ꍇ�́A�t�@�C�������������܂��B �f�B���N�g�����Ȃ��ꍇ�̓f�B���N�g�������܂��B
	 *
	 * @param f
	 * @param dirPath
	 * @return
	 */
	public static boolean move(File f, String dirPath, boolean rename) {
		boolean b = false;
		if (dirPath != null) {
			File dir = new File(dirPath);
			b = FileMoveUtil.moveToDir(f, dir, rename);
		}

		return b;
	}

	public static boolean move(File f, String dirPath) {
		return move(f, dirPath, false);
	}

	/**
	 * ��t�H���_���ċA�I�ɏ���
	 */

	public static void deleteEmptyDir(File dir) {
		for (int i = 0; i < 2; i++) {
			deleteEmptyDirImpl(dir);
			Util.sleep(500l);
		}

	}

	private static void deleteEmptyDirImpl(File dir) {

		File[] listFiles = dir.listFiles();

		if (listFiles == null || listFiles.length == 0) {
			log.info(dir.toString());
			log.info("{}", dir.delete());
			return;
		} else {
			log.info(dir.toString() + " FileCount=" + listFiles.length);
			for (File file : listFiles) {
				if (file.isDirectory()) {
					deleteEmptyDir(file);
				}
			}
		}

		File[] x = new File(dir.getAbsolutePath()).listFiles();
		if (x == null || listFiles.length == 0) {
			log.info(dir.toString());
			log.info("{}", dir.delete());
			return;
		} else {

			log.info(dir.toString() + " FileCount=" + listFiles.length);
			if (listFiles.length < 10) {
				boolean b = false;
				for (File file : x) {
					b = b || file.exists();

				}

				if (b) {
					log.info(dir.toString() + " NO FILE!!!" + dir.delete());

				}

			}

		}

	}

	private static void delete(File dir) {

		for (int i = 0; i < 5; i++) {
			if (dir.delete()) {
				break;
			} else {
				Util.sleep(1000l);
			}
		}

	}

	public static boolean moveParent(File src, String... ext) {
		return moveParent(src, false,ext);
	}

	/**
	 * �e�̃t�H���_�Ƀt�@�C�����W�߂�B ZIP�𓀎��̊K�w�̐�����
	 *
	 * @param src
	 * @param ext
	 *            ����̊g���q������������ꍇ��
	 * @return
	 */
	public static boolean moveParent(File src, boolean rename, String... ext) {

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(src, srcDir);
		Collection<String> allFileFullPath = srcDir.getAllFileFullPath();

		for (String s : allFileFullPath) {

			if (ext == null) {

				move(new File(s), src.getAbsolutePath(), rename);

			} else {

				for (String e : ext) {
					if (s.endsWith(e)) {
						move(new File(s), src.getAbsolutePath(), rename);
						break;
					}

				}

			}

		}

		//���O�̕ύX�ɂ��

		log.info("{}", src);
		int count = src.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File f, String name) {

				return new File(f.getAbsolutePath() + "\\" + name).isFile();

			}
		}).length;

		if (ext == null && allFileFullPath.size() != count) {
			log.info("{}  {}", new Object[] { allFileFullPath.size(), count });
			throw new IllegalStateException("");
		}

		return true;
	}

	/**
	 * �e�̃t�H���_�Ƀt�@�C�����W�߂�B ZIP�𓀎��̊K�w�̐�����
	 *
	 * @param src
	 * @return
	 */
	public static boolean moveParent(File src, boolean rename) {

		return moveParent(src, rename, null);
	}

	/**
	 * �ċN�ŁA�t�H���_�̒��g�����ׂď����B �t�@�C����������B
	 *
	 * @param f
	 */
	public static void deleteDir(File f) {
		if (f.exists() == false) {

			return;
		}

		if (f.isFile()) {
			f.delete();
		}

		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteDir(files[i]);
			}
			f.delete();
		}
	}

	/**
	 * �}���`�X���b�h���ł��t�H���_�������Ȃ��悤�ȃt�H���_���쐬����B
	 */
	public static String createTempDir(String base) throws IOException {

		long millis = System.currentTimeMillis();
		long id = Thread.currentThread().getId();

		String r = base + "/" + id + "_" + millis;
		new File(r).mkdir();

		return r;

	}

	/**
	 * �t�@�C�������쐬����B�ΏۂƂ���f�B���N�g���ɁA�����̃t�@�C�������݂��邩�A �m�F���A�����̃t�@�C�������݂��Ă����ꍇ��
	 * �u���̖��O_����.�g���q�v �Ƃ���������������B�����́A�C���N�������g�����B
	 *
	 * @param filename
	 * @param dir
	 * @return
	 */
	public static File createFileName(String filename, File dir) {
		File newFile = new File(dir.getPath() + "\\" + filename);

		if (newFile.exists()) {
			String base = FilenameUtils.getBaseName(filename);
			String ext = FilenameUtils.getExtension(filename);

			Matcher m = FileUtilExt.fileNoPattern.matcher(base);
			if (m.matches()) {
				String s = m.group(2);
				int i;
				if (s.length() != 0) {
					i = Integer.parseInt(s);
					i++;
				} else {
					i = 1;
				}
				String newFileName = m.group(1) + "_" + i + "." + ext;
				return createFileName(newFileName, dir);
			} else {
				String newFileName = base + "_" + 1 + "." + ext;
				return createFileName(newFileName, dir);
			}

		} else {

			return newFile;

		}
	}

	public static boolean moveToDir(File f, File dir) {

		return moveToDir(f, dir, false);
	}

	/**
	 * �t�@�C���̈ړ����[�e�B���B�t�@�C�����d�����ɂ́A�t�@�C������t���ւ��B
	 *
	 * @param f
	 * @param dir
	 * @return
	 */
	public static boolean moveToDir(File f, File dir, boolean rename) {

		boolean b = false;

		if (rename) {
			String name = NameUtil.createName(f);
			if (!f.getName().equals(name)) {
				log.info(Log.OP, "RENAME {} >> {}", new Object[] { f.getName(),
						name });
				File f2 = new File(f.getParent() + "\\" + name);
				f.renameTo(f2);
				f = f2;

			}
		}

		if (f.getParent().equals(dir.getPath())) {
			log.info("notmove {} == {}", new Object[] { f.getParent(), dir });
			return true;
		}

		File newFile = createFileName(f.getName(), dir);
		try {
			log.info(Log.OP, "move {} >> {}", new Object[] { f, newFile });
			FileUtils.moveFile(f, newFile);
		} catch (Exception e) {
			new IllegalStateException(e);
		}
		return b;
	}

	public static File[] listFiles(File dir, final String... ext) {

		File[] list = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				for (String e : ext) {
					if (name.endsWith(e)) {
						return true;
					}

				}
				return false;

			}
		});

		return list;
	}

}
