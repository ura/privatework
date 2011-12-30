package util.file;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import log.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.StaticUtil;
import util.file.filter.FileNameFilter;
import util.file.filter.FileNameFilter.MODE;
import book.BookNameUtil;
import static util.StaticUtil.sleep;
import static util.file.FileNameUtil.createPath;
import static util.file.FileNameUtil.getExt;
import static util.file.FileNameUtil.getFileName;

/**
 * ファイルの移動、削除、ディレクトリ作成、リネームなどなどのUtil。
 * 操作系に特化。
 *
 *

 *
 */
public class FileOperationUtil {
	static Pattern fileNoPattern = Pattern.compile("(.*)_(\\d*)");
	private static Logger log = LoggerFactory
			.getLogger(FileOperationUtil.class);

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
			b = FileOperationUtil.moveToDir(f, dir, rename);
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

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(dir, srcDir);
		Collection<Dir> values = srcDir.dirSet.values();

		for (Dir dir2 : values) {
			log.debug(dir2.toString() + "\t hasFolder:" + dir2.hasFolder());

			if (dir2.isEmpty()) {
				log.info("削除対象:" + dir2.toString());
				dir2.delete();
			}
		}

	}

	/**
	 * 空フォルダを再帰的に消す。
	 * 拡張子が指定されている場合、それ以外のファイルは必要ないものとみなし
	 * 指定されたファイルが入ってないフォルダは削除対象になる。
	 */
	public static void deleteEmptyDir(File dir, String... ext) {

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(dir, srcDir);
		Collection<Dir> values = srcDir.dirSet.values();

		for (Dir dir2 : values) {
			log.debug(dir2.toString() + "\t hasFolder:" + dir2.hasFolder());

			if (dir2.isEmpty(new FileNameFilter(MODE.EXT_INCLUDE, ext))) {
				log.info("削除対象:" + dir2.toString());
				dir2.deleteForce();
			}
		}

	}

	/**
	 * 強制的に再起で消す。
	 */
	public static void deleteForce(File dir) {

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(dir, srcDir);

		for (Dir dir2 : srcDir.dirSet.values()) {
			dir2.deleteForce();

		}
		dir.delete();

	}

	public static boolean delete(File dir) {

		for (int i = 0; i < 5; i++) {
			if (dir.delete()) {

				return true;
			} else {
				StaticUtil.sleep(500l);
			}
		}

		log.warn("[{}]が消えません", dir.getAbsolutePath());
		return false;

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
		Collection<File> allFileFullPath = srcDir.getAllFile();

		for (File s : allFileFullPath) {

			if (ext == null) {

				move(s, src.getAbsolutePath(), rename);

			} else {

				for (String e : ext) {
					if (s.getName().endsWith(e)) {
						move(s, src.getAbsolutePath(), rename);
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

	/**
	 * 下位にあるフォルダをすべてSRC直下に移動する
	 *
	 * @param src
	 * @return
	 */
	public static boolean moveFolderToParent(File src) {

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(src, srcDir);
		Collection<Dir> values = srcDir.dirSet.values();
		log.debug("Dir Count" + values.size());

		for (Dir dir : values) {
			log.info(dir.dir.getPath());

			File srcPath = dir.dir;
			File destPath = FileNameUtil.createNewPath(src, dir.dir);

			log.info(srcPath.getAbsolutePath() + "\t" + srcPath.exists() + "\t"
					+ destPath.getAbsolutePath() + "\t" + destPath.exists());

			if (srcPath.exists()) {
				try {
					Files.move(srcPath.toPath(), destPath.toPath(),
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
	 * マルチスレッド時でもフォルダ名が被らないようなフォルダを作成する。
	 */
	public static File createTempDir(String base) throws IOException {

		return createTempDir(base, "");

	}

	/**
	 * マルチスレッド時でもフォルダ名が被らないようなフォルダを作成する。
	 */
	public static File createTempDir(String base, String head)
			throws IOException {

		long millis = System.currentTimeMillis();
		long id = Thread.currentThread().getId();

		String r = base + "/" + head + "_" + id + "_" + millis;
		new File(r).mkdir();

		return new File(r);

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

			Matcher m = fileNoPattern.matcher(base);
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
	 * 正規表現に一致したファイル名のファイルを削除します。
	 * @param dir
	 */
	public static void removeFile(File dir, String[] list) {
		List<Pattern> r = new ArrayList<Pattern>();
		for (String string : list) {
			Pattern compile = Pattern.compile(string);
			r.add(compile);
		}
		removeFile(dir, r.toArray(new Pattern[0]));

	}

	/**
	 * 正規表現に一致したファイル名のファイルを削除します。
	 * @param dir
	 */
	public static void removeFile(File dir, Pattern[] list) {

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(dir, srcDir);
		Collection<Dir> values = srcDir.dirSet.values();

		for (Dir dir2 : values) {
			SortedSet<File> fileNameSet = dir2.fileSet;
			for (File f : fileNameSet) {

				for (Pattern p : list) {
					if (p.matcher(f.getName()).find()) {
						log.warn("不要なファイルを削除します。　{}", f.getName());
						f.delete();
					}
				}
			}
		}
	}

	/**
	 * 指定されたフォルダを再帰して、チェックしファイル名を単純化（アルファベット、数値のみ）します。
	 * @param dir
	 */
	public static void renameToSimpleFileName(File dir) {

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(dir, srcDir);
		Collection<Dir> values = srcDir.dirSet.values();

		for (Dir dir2 : values) {
			SortedSet<File> fileNameSet = dir2.fileSet;
			Map<File, File> nameMap = BookNameUtil
					.createSimpleName(fileNameSet);

			for (File f : fileNameSet) {

				File dest = nameMap.get(f);

				if (f.equals(dest)) {
					//ファイル名に変更がなければ無視
					continue;
				}

				if (!dest.exists()) {
					boolean b = f.renameTo(dest);
					if (!b) {
						throw new IllegalStateException("RENAME不能:"
								+ f.getAbsolutePath() + " >>" + dest.getPath());
					}
				} else {

					//TODO ☆このロジック入らなくなるはず。重複しないように内容に名称を作るので、

					log.warn("他のファイル名と重複:" + f.getAbsolutePath() + ">>"
							+ dest.getName());

					if (f.length() == dest.length()) {
						log.warn("変換前と変換後の名称、ファイルサイズが同じため、同じファイルとみなし削除します。:"
								+ f.getAbsolutePath() + ">>" + dest.getName());
						f.delete();
						continue;
					}

					String ext = getExt(dest);
					String content = getFileName(dest);
					File dest2 = createPath(dir2.dir, "z" + content + "." + ext);
					log.warn("リネーム名変更:" + f.getAbsolutePath() + ">>"
							+ dest2.getName());
					boolean b = f.renameTo(dest2);
					if (!b) {
						throw new IllegalStateException("他のファイル名と重複:\n"
								+ f.getAbsolutePath() + ">>" + dest.getName());
					}
				}

			}

		}

	}

	public static boolean renameTo(File src, File dest) {
		int i = 0;
		while (!src.renameTo(dest)) {
			sleep(100);
			i++;

			if (i > 200) {
				log.warn("ファイルのリネームに失敗しました。 {} >> {}", src, dest);
				throw new IllegalStateException("ファイルのリネームに失敗しました。 " + src
						+ " >> " + dest);
			}
		}

		return true;
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
			String name = BookNameUtil.createSimpleName(f);
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