package util.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import util.file.filter.DirFilter;
import zip.State;
import zip.ZipChecker;
import conf.ConfConst;
import dir.Dir;
import dir.DirCollector;

public class FileUtilExt extends ObjectUtil {
	static Pattern fileNoPattern = Pattern.compile("(.*)_(\\d*)");
	private static Logger log = LoggerFactory.getLogger(FileUtilExt.class);

	private static final String WORK_DIR = ConfConst.MAIN_CONF
			.getVal(ConfConst.ARC_WORK_DIR);

	/**
	 * パスワードつきのファイルを削除します。
	 *
	 */
	public static void movePassZipAll(String src) {

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(new File(src), srcDir);
		Collection<String> allFileFullPath = srcDir.getAllFileFullPath();

		File moveDir = new File(src + "\\" + "ゴミ箱");
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
				FileOperationUtil.moveToDir(f,
						State.ZIP_OPEN_ERROR.getDir(moveDir));
				break;
			case FEW_FILE:
				log.info(Log.OP, "Delete PASSWORD file:{}", string);
				FileOperationUtil.moveToDir(f, State.FEW_FILE.getDir(moveDir));
				break;
			case UNZIP_ERROR:
				log.info(Log.OP, "Delete PASSWORD file:{}", string);
				FileOperationUtil.moveToDir(f,
						State.UNZIP_ERROR.getDir(moveDir));
				break;
			case OTHER:
				log.info(Log.OP, "Delete PASSWORD file:{}", string);
				FileOperationUtil.moveToDir(f, State.OTHER.getDir(moveDir));

				break;

			default:
				log.error(Log.OP, "不正なswitch file:{}", string);

				break;

			}

		}

		checker.save();
	}

	/**
	 * 圧縮ファイルの形式を変えます。 rar→zipに。
	 *  さらに、入れ子圧縮の場合、入れ子の展開を行う。
	 *  使用していない感じ
	 *
	 *  @deprecated
	 *
	 */
	public static void convertArc(String srcArcFile) {

		File srcFile = new File(srcArcFile);

		try {
			String work = FileOperationUtil.createTempDir(WORK_DIR);
			WinRARWrapper.decode(srcArcFile, work);

			//TODO フォルダのアップ戦略を検討する

			File dir = new File(work);

			// 解凍後のフォルダ内で、深いところにアーカイブがある場合、
			// 直下に持ってきて、それのみをリストする
			FileOperationUtil.moveParent(dir, "zip", "rar");
			File[] list = FileOperationUtil.listFiles(dir, ".rar", ".zip");

			for (File zipFile : list) {

				//TODO 巻数の戦略を書き換える

				String childDir = work + "/" + NameUtil.kan(zipFile);
				File cDir = new File(childDir);

				// 解凍して、フォルダ内のファイルを全部上に上げる。
				decodeAll(cDir, srcFile);

			}

			//TODO 失敗した場合に備え、フォルダ名を変えてから圧縮する

			WinRARWrapper.encode(work, WORK_DIR + "/"
					+ srcFile.getName().replace("rar", "zip"));

		} catch (IOException e) {
			log.error("解凍時に想定外エラー", e);
		} catch (InterruptedException e) {
			log.error("解凍時に想定外エラー", e);
		} catch (Exception e) {
			log.error("解凍時に想定外エラー", e);
		}

	}

	public static void decodeAll(File workDir, File arcFile)
			throws IOException, InterruptedException {
		decodeAll(workDir, arcFile, false);
	}

	public static void decodeAll(File workDir, File arcFile, boolean del)
			throws IOException, InterruptedException {

		WinRARWrapper.decode(arcFile, new File(workDir.getAbsolutePath()
				+ File.separatorChar + arcFile.getName()));
		if (del) {
			arcFile.delete();
		}

		KeywordFileCollector coll = new KeywordFileCollector(".rar", ".zip");
		new FileWalker().walk(workDir, coll);

		List<File> list = coll.getFiles();

		for (File file : list) {
			decodeAll(workDir, file, true);
		}

	}

	/**
	 * 圧縮ファイルの統廃合を行います。 1巻、2巻、3巻と分かれているファイルを結合し、一個のファイルにします。
	 *
	 *
	 */
	public static void rebuildArc(String base, String name, String... keword) {

		KeywordFileCollector coll = new KeywordFileCollector(keword);
		new FileWalker().walk(new File(base), coll);

		try {

			List<File> files = coll.getFiles();

			Collection<File> newList = UserInput.selectManySwing(files);
			for (File file : newList) {
				log.info("rebuildArc target {}", file.toString());
			}

			rebuildArc(name, newList);

			for (File zipFile : files) {
				FileOperationUtil.move(zipFile, "L:\\tmp");
			}

		} catch (IOException e) {
			log.error("解凍時に想定外エラー", e);
		} catch (InterruptedException e) {
			log.error("解凍時に想定外エラー", e);
		} catch (Exception e) {
			log.error("解凍時に想定外エラー", e);
		}

	}

	public static void rebuildArc(String name, Collection<File> newList)
			throws IOException, InterruptedException {
		String work = FileOperationUtil.createTempDir(WORK_DIR);
		File workF = new File(work);

		for (File zipFile : newList) {

			decodeAll(workF, zipFile);

		}

		FileOperationUtil.moveFolderToParent(workF);
		FileOperationUtil.deleteEmptyDir(workF, "jpeg", "jpg");

		File[] dirs = workF.listFiles(new DirFilter());

		for (File dir : dirs) {
			String no = NameUtil.bookNo(dir.getName());
			File newDir = new File(dir.getParent() + File.separatorChar + no);
			boolean b = false;
			if (!newDir.exists()) {
				b = dir.renameTo(newDir);
			}

			if (!b) {
				log.warn("フォルダのリネームに失敗しました:" + no);
			}
		}

		WinRARWrapper.encode(work,
				WORK_DIR + "/" + NameUtil.createCominName(name, newList));

	}

	/**
	 * 似たファイルが入っているディレクトリを見つけ、 フォルダを新規に作成、移動を行います。
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
	 * 同じファイルを削除します。
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
			// TODO 大きいファイルは、CRCが重いかと思っていたが、とりあえず、やってみる方針で
			// 1000M以下だったら
			if (e.getKey().longValue() < 5000 * 1000 * 1000l) {
				deleteSamaFileByCRC(e.getValue());
			} else {

			}
		}
	}

	private static void deleteSamaFileByCRC(List<File> list) {
		MapList<Long, File> mapList = new MapList<Long, File>();

		if (list.size() == 1) {
			log.info("このファイルは重複の可能性がないため、スキップします。 FILE {}", list.get(0));
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
					log.info("重複CRC [{}] FILE {}", e.getKey(), file.getName());
				}
			}
			List<File> deleteFile = deleteFile(value);

			list.removeAll(deleteFile);

		}

	}

	/**
	 * ひとつのファイルを残して削除します。
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
	 * ファイル名のフィルタつき、分類。
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
	 * ファイルを分類します。複数のファイルを同時に対象にします。
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
	 * ファイルを分類します。
	 *
	 * @param dirs
	 * @param fileName
	 * @return
	 */
	public static boolean classify(Collection<Dir> dirs, String fileName) {
		return classify(dirs, fileName, ScoreUtil.createDefault());
	}

	/**
	 * ファイルを分類します。
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
			FileOperationUtil.moveToDir(new File(filePath), nearDir.dir);
			return true;
		} else {
			return false;
		}
	}

}
