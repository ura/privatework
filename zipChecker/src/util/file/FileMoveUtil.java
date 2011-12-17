package util.file;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.regex.Matcher;

import log.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.NameUtil;
import util.Util;
import dir.Dir;
import dir.DirCollector;

/**
 * ファイルの移動、削除、ディレクトリ作成、リネームなどなどのUtil
 *

 *
 */
public class FileMoveUtil {

	static Logger log = LoggerFactory.getLogger(FileMoveUtil.class);

	/**
	 * ファイルをディレクトリに移動します。 ファイル名が重複している場合は、ファイル名をつけ直します。 ディレクトリがない場合はディレクトリを作ります。
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
	 * 空フォルダを再帰的に消す
	 */

	public static void deleteEmptyDir(File dir) {
		for (int i = 0; i < 2; i++) {
			deleteEmptyDirImpl(dir);
			Util.sleep(500l);
		}

	}

	/**
	 * 変なロジックに見えるが、遅延をしないと消したファイルが見えるとか、
	 * 消せないとかそんなんだったはず。
	 * @param dir
	 */
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
		return moveParent(src, false, ext);
	}

	/**
	 * 親のフォルダにファイルを集める。 ZIP解凍時の階層の整理に
	 *
	 * @param src
	 * @param ext
	 *            特定の拡張子だけ引っ張る場合に
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

		//名前の変更により

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

	public static boolean moveFolderToParent(File src) {

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(src, srcDir);
		Collection<Dir> values = srcDir.dirSet.values();
		for (Dir dir : values) {
			Path srcPath = Paths.get(dir.dir.getAbsolutePath());
			Path destPath = Paths.get(src.getAbsolutePath()
					+ File.separatorChar + dir.dir.getName());

			log.info(srcPath.toFile().getAbsolutePath() + "\t"
					+ srcPath.toFile().exists() + "\t"
					+ destPath.toFile().getAbsolutePath() + "\t"
					+ destPath.toFile().exists());

			if (srcPath.toFile().exists() || destPath.toFile().exists()) {
				try {
					Files.move(srcPath, destPath,
							StandardCopyOption.ATOMIC_MOVE);
				} catch (IOException e) {
					log.error("COPYに失敗", e);
					throw new IllegalStateException();
				}
			}

		}

		return true;
	}

	/**
	 * 親のフォルダにファイルを集める。 ZIP解凍時の階層の整理に
	 *
	 * @param src
	 * @return
	 */
	public static boolean moveParent(File src, boolean rename) {

		return moveParent(src, rename, null);
	}

	/**
	 * 再起で、フォルダの中身をすべて消す。 ファイルも消える。
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
	 * マルチスレッド時でもフォルダ名が被らないようなフォルダを作成する。
	 */
	public static String createTempDir(String base) throws IOException {

		long millis = System.currentTimeMillis();
		long id = Thread.currentThread().getId();

		String r = base + "/" + id + "_" + millis;
		new File(r).mkdir();

		return r;

	}

	/**
	 * ファイル名を作成する。対象とするディレクトリに、同名のファイルが存在するか、 確認し、同名のファイルが存在していた場合は
	 * 「元の名前_数字.拡張子」 といった処理をする。数字は、インクリメントされる。
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
	 * ファイルの移動ユーティル。ファイル名重複時には、ファイル名を付け替え。
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
