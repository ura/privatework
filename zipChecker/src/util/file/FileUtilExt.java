package util.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import log.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import socre.ScoreUtil;
import util.BookNameUtil;
import util.CollectionUtil;
import util.MapList;
import util.UserInput;
import util.WinRARWrapper;
import util.file.filter.DirFilter;
import util.file.filter.FileNameFilter;
import util.file.filter.FileNameFilter.MODE;
import webapi.BookInfo;
import zip.State;
import zip.ZipChecker;
import conf.ConfConst;
import dir.Dir;
import dir.DirCollector;
import static util.file.FileNameUtil.createPath;
import static util.file.FileNameUtil.getFileName;

/**
 * フォルダの整理関連に特化。
 * 圧縮ファイルの整理、フォルダの整理などを集約
 *
 */
public class FileUtilExt {
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

	public static void decodeAll(File workDir, File arcFile)
			throws IOException, InterruptedException {
		decodeAll(workDir, arcFile, false);
	}

	public static void decodeAll(File workDir, File arcFile, boolean del)
			throws IOException, InterruptedException {

		File newWorkDir = createPath(workDir, getFileName(arcFile));

		WinRARWrapper.decode(arcFile, newWorkDir);
		if (del) {
			arcFile.delete();
		}

		KeywordFileCollector coll = new KeywordFileCollector(".rar", ".zip");
		new FileWalker().walk(newWorkDir, coll);

		List<File> list = coll.getFiles();

		for (File file : list) {
			decodeAll(newWorkDir, file, true);
		}

	}

	/**
	 * 圧縮ファイルの統廃合を行います。
	 * 1巻、2巻、3巻と分かれているファイルを結合し、一個のファイルにします。
	 * ファイルの選択UIを持ちます。
	 *
	 *
	 */
	public static void rebuildArcWithUI(String base, String name,
			String... keword) {

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

	public static void rebuildArc(String name, File[] array)
			throws IOException, InterruptedException {
		ArrayList<File> l = new ArrayList<File>();
		for (File file : array) {
			l.add(file);

		}
		rebuildArc(name, l);

	}

	public static void decodeAll(File workDir, Collection<File> newList)
			throws IOException {

		try {
			ExecutorService ex = Executors.newFixedThreadPool(5);
			List<Callable<Void>> l = new ArrayList<>();
			for (File zipFile : newList) {
				if (zipFile.isFile()) {
					l.add(new DecodeTask<Void>(workDir, zipFile));
				} else {
					log.warn("アーカイブのみを渡してください {}", zipFile.getName());
				}
			}
			List<Future<Void>> invokeAll = ex.invokeAll(l);
			for (Future<Void> future : invokeAll) {
				future.get();
			}
		} catch (InterruptedException e) {
			log.error("解凍時に例外が発生しました。", e);
			throw new IllegalStateException(e);
		} catch (ExecutionException e) {
			log.error("解凍時に例外が発生しました。", e);
			throw new IllegalStateException(e);
		}

	}

	static class DecodeTask<Void> implements Callable<Void> {

		public DecodeTask(File work, File arc) {
			super();
			this.work = work;
			this.arc = arc;
		}

		private File work;
		private File arc;

		@Override
		public Void call() throws Exception {

			decodeAll(work, arc);

			return null;
		}

	}

	public static void rebuildArc(String name, Collection<File> newList)
			throws IOException, InterruptedException {

		File workF = FileOperationUtil.createTempDir(WORK_DIR);

		decodeAll(workF, newList);
		FileOperationUtil.moveFolderToParent(workF);
		FileOperationUtil.deleteEmptyDir(workF, "jpeg", "jpg");
		FileOperationUtil.renameFiles(workF);
		FileOperationUtil.removeFile(workF, new String[] { "^.*\\.html$",
				"^.*\\.url$", "^.*\\.txt$", "^Thumbs\\.db", "^[^.]*$" });

		File[] dirs = workF.listFiles(new DirFilter());

		SortedMap<BookInfo, File> s = new TreeMap<BookInfo, File>();
		List<Object[]> temp = new ArrayList<>();
		for (File dir : dirs) {
			//
			BookInfo bookNo = BookNameUtil.bookInfoFromBarcode(dir);
			File newDir = createPath(dir.getParent(), bookNo.getInfo());

			log.debug(newDir.getName() + "  " + dir.getName());
			s.put(bookNo, newDir);
			log.debug(newDir.getName() + "  " + s.size());

			temp.add(new Object[] { dir, newDir, bookNo });

		}
		for (Object[] objects : temp) {
			moveDir((File) objects[0], (File) objects[1], (BookInfo) objects[2]);
		}

		BookNameUtil.createCominName(new File(WORK_DIR), s);

	}

	private static void moveDir(File src, File dest, BookInfo bookNo)
			throws IOException {
		boolean b = false;
		//TODO フォルダがかぶった場合の処理を入れる サイズを見て判断するか、末尾に数字を付けて臨時対応
		if (!dest.exists()) {
			b = FileOperationUtil.renameTo(src, dest);
			if (!b) {
				log.warn("フォルダのリネームに失敗しました:" + bookNo);
			}
		} else {

			if (!isSame(src, dest)) {

				long srcSize = dirSize(src);
				long destSize = dirSize(dest);

				if (srcSize < destSize) {
					File tempDir = FileOperationUtil.createTempDir(WORK_DIR);
					File path = createPath(tempDir, bookNo.getInfo());
					FileOperationUtil.renameTo(src, path);

					log.warn("サイズの小さいフォルダをテンポラリに移しました:{}  :{}  {}",
							new Object[] { path, srcSize, destSize });
				} else {
					File tempDir = FileOperationUtil.createTempDir(WORK_DIR);
					File path = createPath(tempDir, bookNo.getInfo());
					FileOperationUtil.renameTo(dest, path);

					log.warn("サイズの小さいフォルダをテンポラリに移しました:{}  :{}  {}",
							new Object[] { path, "" + srcSize, destSize });

				}

				//throw new IllegalStateException();
			}

		}
	}

	/**
	 * フォルダ内のファイルサイズが同一だった場合、同じフォルダとみなします。
	 * （上書きの判断などに使用）
	 * @param src
	 * @param dest
	 * @return
	 */
	private static boolean isSame(File src, File dest) {

		if (src.equals(dest)) {
			return true;
		} else {
			long srcSize = dirSize(src);
			long destSize = dirSize(dest);

			if (srcSize == destSize) {

				log.warn("フォルダのサイズが一致しました。同じフォルダとみなします。{} : {} ", src, dest);
				return true;
			} else {
				log.info("フォルダのサイズが一致しましませんでした。{} : {} ", src, dest);
				return false;
			}
		}

	}

	private static long dirSize(File dir) {
		File[] filse = dir.listFiles(new FileNameFilter(MODE.EXT_EXCLUDE, "z"));
		long l = 0;
		for (File file : filse) {
			l = +file.length();
		}

		return l;
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
