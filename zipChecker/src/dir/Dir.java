package dir;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import socre.name.FileNameParseCoreOnly;
import socre.name.FileNameParser;
import util.CollectionUtil;
import util.CollectionUtil.Counter;
import util.StaticUtil;
import util.StringUtil;
import util.file.FileOperationUtil;
import util.file.filter.DirFilter;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Dir implements Comparable<Dir> {

	private static Logger log = LoggerFactory.getLogger(Dir.class);

	public Dir(File dir) {
		super();
		this.dir = dir;
		log.debug(dir.getAbsolutePath());

		String path = dir.getPath();

		String[] args = path.split("\\\\");
		nameSet = new TreeSet<String>();
		fileNameSet = new TreeSet<String>();

		for (String s : args) {
			nameSet.add(s);
		}

	}

	public File dir;

	/**
	 * パスごとの要素一覧。
	 * 各階層のフォルダ名を表す
	 */
	public SortedSet<String> nameSet;

	/**
	 * 子ファイルのセット
	 * フォルダは含まない
	 */
	public SortedSet<String> fileNameSet;

	@Inject
	@Named("core")
	private FileNameParser fileNameParser;

	public void addFile(File f) {
		fileNameSet.add(f.getPath());
	}

	public void refreshFileInfo() {

		SortedSet<String> s = new TreeSet<String>();
		for (String fname : fileNameSet) {
			if (new File(fname).exists()) {
				s.add(fname);
			}
		}
		this.fileNameSet = s;
	}

	/**
	 * 必要なファイルを示すフィルターを渡して、フォルダが必要か判定する。
	 * @param filter
	 * @return
	 */
	public boolean isEmpty(FileFilter filter) {

		refreshFileInfo();

		return !(hasFolder() || (!StaticUtil.isEmpty(dir.listFiles(filter))));

	}

	/**
	 * 必要なファイルを示すフィルターを渡して、フォルダが必要か判定する。
	 * @param filter
	 * @return
	 */
	public void print(FileFilter filter) {

		refreshFileInfo();
		File[] listFiles = dir.listFiles(filter);

		for (File file : listFiles) {
			log.info(file.getAbsolutePath());
		}

	}

	public boolean isEmpty() {

		refreshFileInfo();
		return !(hasFolder() || fileNameSet.size() > 0);

	}

	public long getChildrenfileSize() {

		refreshFileInfo();

		long l = 0;
		for (String str : this.fileNameSet) {
			File file = new File(str);
			l = +file.length();

		}

		return l;

	}

	public boolean delete() {
		return FileOperationUtil.delete(dir);
	}

	/**
	 * フォルダの内容があっても消します。
	 * でも、小フォルダにファイルが入っていると多分消えません。
	 */
	public void deleteForce() {

		File[] files = dir.listFiles();
		for (File file : files) {
			FileOperationUtil.delete(file);
		}
		FileOperationUtil.delete(dir);

	}

	public boolean hasFolder() {
		File[] dirs = dir.listFiles(new DirFilter());
		return !StaticUtil.isEmpty(dirs);

	}

	//TODO ここに機能があるのがいいのかは検討。未完成
	/**
	 * ディレクトリの中を確認し、ファイルの類似性を見つけ、フォルダ作成、および、MOVEを行う。
	 *
	 */
	public void createNewDir() {
		//TODO パーサの入れ替え
		FileNameParser fileNameParser = new FileNameParseCoreOnly();

		Map<String, CollectionUtil.Counter> map = null;

		//フォルダの状態を分析
		for (String dirFile : this.fileNameSet) {
			map = CollectionUtil.count(map,
					fileNameParser.parse(FilenameUtils.getName(dirFile)),
					fileNameSet.size());
		}
		if (map != null) {
			SortedSet<MoveFiles> sort = new TreeSet<MoveFiles>();
			for (Entry<String, Counter> e : map.entrySet()) {
				//そのフォルダを特徴となる要素ではなく、
				//かつ一定数のふぁいるがあったら
				if (e.getValue().per() < 50 && e.getValue().count > 6) {

					//親のパスで定義している名称ではフォルダを作らない。
					if (!nameSet.contains(e.getKey())) {
						sort.add(new MoveFiles(e.getKey(), e.getValue().count));
					}
				}
			}
			//とりあえず、ふりわけ
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
	 * 移動検討時の管理用のクラス

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
				FileOperationUtil.move(new File(file), getNewDir());
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dir == null) ? 0 : dir.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dir other = (Dir) obj;
		if (dir == null) {
			if (other.dir != null)
				return false;
		} else if (!dir.equals(other.dir))
			return false;
		return true;
	}

	@Override
	public int compareTo(Dir o) {

		return -dir.getAbsolutePath().compareTo(o.dir.getAbsolutePath());
	}

}
