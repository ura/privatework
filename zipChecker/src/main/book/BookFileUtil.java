package book;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import log.Log;
import module.InjectorMgr;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import socre.ScoreUtil;
import util.CollectionUtil;
import util.UserInput;
import util.WinRARWrapper;
import util.file.Checker;
import util.file.Dir;
import util.file.DirCollector;
import util.file.FileOperationUtil;
import util.file.FileWalker;
import util.file.KeywordFileCollector;
import zip.State;
import zip.ZipChecker;
import book.webapi.BookInfo;
import conf.ConfConst;
import static util.file.FileNameUtil.createPath;
import static util.file.FileNameUtil.getFileName;
import static util.file.FileNameUtil.haveExt;

/**
 * フォルダの整理関連に特化。
 * 圧縮ファイルの整理、フォルダの整理などを集約
 *
 */
public class BookFileUtil {

	private static Logger log = LoggerFactory.getLogger(BookFileUtil.class);

	private static final String WORK_DIR = ConfConst.MAIN_CONF
			.getVal(ConfConst.ARC_WORK_DIR);
	private static final int THREAD_DECODE = ConfConst.MAIN_CONF
			.getInt(ConfConst.THREAD_DECODE);

	private static BookNameUtil bookName = InjectorMgr.get().getInstance(
			BookNameUtil.class);

	/**
	 * パスワードつきのファイルを削除します。
	 *
	 */
	public static void movePassZipAll(String src) {

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(new File(src), srcDir);
		Collection<String> allFileFullPath = srcDir.getAllFilePath();

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

		try {
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
		} catch (IOException e) {

			log.error("解凍に失敗したファイルが存在します。対処を検討指定ください。{}",
					arcFile.getAbsolutePath());
			FileOperationUtil.deleteForce(newWorkDir);

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
			ExecutorService ex = Executors.newFixedThreadPool(THREAD_DECODE);
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

	/**
	 * 拡張子が存在しないファイルに対し画像か確認を行います。
	 * 画像だった場合は、JPEGとみなし、リネームします。
	 * @param root
	 */
	public static void jpgCheck(File root) {
		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(root, srcDir);
		Collection<File> allFileFullPath = srcDir.getAllFile();
		Set<File> noExtSet = new HashSet<>();
		for (File file : allFileFullPath) {
			if (!haveExt(file)) {
				noExtSet.add(file);
			}
		}

		for (File file : noExtSet) {
			if (isImage(file)) {
				File dest = new File(file.getAbsoluteFile() + ".jpg");
				log.warn("拡張子がないファイルがありましたが、これは画像でした。{}", file);
				if (!file.renameTo(dest)) {
					throw new IllegalStateException(file.getAbsolutePath());
				}
			}

		}

	}

	public static boolean isImage(File f) {
		try {
			BufferedImage image = ImageIO.read(f);
			if (image != null) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			log.error("想定外です。画像でない場合はNULLが返りECEPTIONは発生しません。{}", f, e);
			throw new IllegalArgumentException(
					"想定外です。画像でない場合はNULLが返りECEPTIONは発生しません。");
		}
	}

	public static void rebuildArc(String name, Collection<File> newList)
			throws IOException, InterruptedException {
		File workF = null;
		if (true) {
			workF = FileOperationUtil.createTempDir(WORK_DIR);

			decodeAll(workF, newList);
		}
		if (true) {
			//workF = new File("G:\\arkwork\\_1_1325925926356");
			FileOperationUtil.moveFewFile(workF);
			FileOperationUtil.moveFolderToParent(workF);
			jpgCheck(workF);
			FileOperationUtil.deleteEmptyDir(workF, "jpeg", "jpg", "png");

			FileOperationUtil.removeFile(workF, new String[] { "^.*\\.html$",
					"^.*\\.url$", "^.*\\.txt$", "^Thumbs\\.db", "^[^.]*$",
					"spot\\.com\\.jpg" });
			FileOperationUtil.renameToSimpleFileName(workF);

			Map<File, BookInfo> allbookInfo = bookName.getAllbookInfo(workF);

			SortedMap<BookInfo, File> s = new TreeMap<BookInfo, File>();

			File tempDest1 = FileOperationUtil.createTempDir(WORK_DIR, "名称重複");
			File tempDest2 = FileOperationUtil.createTempDir(WORK_DIR, "完全一致");

			boolean flag = false;
			for (Entry<File, BookInfo> e : allbookInfo.entrySet()) {
				File src = e.getKey();
				BookInfo bookNo = e.getValue();

				File newDir = createPath(workF, bookNo.getInfo());

				s.put(bookNo, newDir);

				try {
					moveDir(src, newDir, bookNo, tempDest1, tempDest2);
				} catch (Exception e1) {
					flag = true;
				}
			}
			if (flag) {
				return;
			}

			bookName.createCominName(new File(WORK_DIR), s);
			bookName.getBookInfoRepo().save();
		}

	}

	/**
	 * ディレクトリの移動を行います。
	 * ディレクトリのサイズ等を確認し、重複確認等を実施します。
	 * @param src
	 * @param dest
	 * @param bookNo
	 * @throws IOException
	 */
	private static void moveDir(File src, File dest, BookInfo bookNo,
			File tempDest1, File tempDest2) throws IOException {
		boolean b = false;
		log.info("下記の移動を検討します。{} >> {}", src, dest);
		if (!src.equals(dest)) {
			if (!dest.exists()) {
				b = FileOperationUtil.renameTo(src, dest);
				if (!b) {
					log.warn("フォルダのリネームに失敗しました:" + bookNo);
				}
			} else {
				long srcSize = dirSize(src);
				long destSize = dirSize(dest);
				if (notSame(src, tempDest2)) {

					if (srcSize < destSize) {

						File path = createPath(tempDest1, bookNo.getInfo());
						FileOperationUtil.renameTo(src, path);

						log.warn("サイズの小さいフォルダをテンポラリに移しました:{}  :{}K  {}K",
								new Object[] { path, srcSize / 1024,
										destSize / 1024 });
					} else {

						File path = createPath(tempDest1, bookNo.getInfo());
						FileOperationUtil.renameTo(dest, path);
						FileOperationUtil.renameTo(src, dest);

						log.warn("サイズの小さいフォルダをテンポラリに移しました:{}  :{}K  {}K",
								new Object[] { path, srcSize / 1024,
										destSize / 1024 });

					}
				} else {
					File path = createPath(tempDest2, bookNo.getInfo());
					FileOperationUtil.renameTo(src, path);
					log.warn("フォルダサイズが一緒でした。テンポラリに移動します。:{}  :{}K  {}K",
							new Object[] { path, srcSize / 1024,
									destSize / 1024 });
				}

			}
		} else {
			log.info("送り先と送り元が同様でした。{} >> {}", src.getAbsolutePath(),
					dest.getAbsolutePath());
		}

	}

	/**
	 * フォルダ内のファイルサイズが同一だった場合、同じフォルダとみなします。
	 * （上書きの判断などに使用）
	 * @param src
	 * @param dest
	 * @return
	 */
	private static boolean notSame(File src, File dest) {

		if (src.equals(dest)) {
			return false;
		} else {
			long srcSize = dirSize(src);
			long destSize = dirSize(dest);

			int i = src.list().length;
			int j = dest.list().length;

			if (srcSize == destSize && i == j) {

				log.warn("フォルダのサイズが一致しました。同じフォルダとみなします。{} : {} ", src, dest);
				return false;
			} else {
				log.info("フォルダのサイズが一致しましませんでした。{} : {} ", src, dest);
				return true;
			}
		}

	}

	private static long dirSize(File dir) {
		return FileUtils.sizeOfDirectory(dir);
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
	 * ファイル名のフィルタつき、分類。
	 *
	 * @param src
	 * @param nameFilter
	 * @param dest
	 */
	public static void classifyAll(String src, String[] nameFilter, String dest) {

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(new File(src), srcDir);

		Collection<String> allFileFullPath = srcDir.getAllFilePath();
		CollectionUtil.nameFilter(allFileFullPath, nameFilter, false);

		DirCollector destDir = new DirCollector();
		new FileWalker().walk(new File(dest), destDir);

		BookFileUtil.classifyAll(destDir.dirSet.values(), allFileFullPath);

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
