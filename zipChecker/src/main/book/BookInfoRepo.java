package book;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.file.Dir;
import util.file.DirCollector;
import util.file.ObjectUtil;
import book.webapi.BookInfo;
import book.webapi.BookInfoFromWeb;
import static util.file.FileNameUtil.getExt;

/**
 * 新刊の探査をつける。
 * ステータスをつける。
 * @author name
 *
 */
public class BookInfoRepo implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 610289580264110233L;

	private static Logger log = LoggerFactory.getLogger(BookInfoRepo.class);
	private Map<Key, BookInfo> map = new HashMap<Key, BookInfo>();

	public enum State implements Serializable {
		HAVE, WANT, BAT, NEEDLESS, DUMMY
	};

	static class Key implements Serializable {
		public Key(String isbn, State state) {
			super();
			this.isbn = isbn;
			this.state = state;
		}

		/**
		 *
		 */
		private static final long serialVersionUID = 4935627485713044172L;
		String isbn;
		State state;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((isbn == null) ? 0 : isbn.hashCode());
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
			Key other = (Key) obj;
			if (isbn == null) {
				if (other.isbn != null)
					return false;
			} else if (!isbn.equals(other.isbn))
				return false;
			return true;
		}

	}

	public void addHave(BookInfo info) {
		map.put(new Key(info.getIsbn(), State.HAVE), info);
	}

	/**
	 * 持っていない候補を突っ込む。
	 * @param info
	 */
	private void addBatch(Set<BookInfo> bookInfos) {
		for (BookInfo info : bookInfos) {
			//書籍情報は編集されている可能性があるため。ISBN出検索する
			if (!map.containsKey(new Key(info.getIsbn(), State.DUMMY))) {
				map.put(new Key(info.getIsbn(), State.BAT), info);
			}
		}

	}

	/**
	 * ステータスの更新
	 * @param infos
	 * @param state
	 */
	public void update(Set<BookInfo> infos, State state) {
		for (BookInfo info : infos) {
			map.put(new Key(info.getIsbn(), state), info);
		}

	}

	/**
	 * 状態を指定して一覧を出す。
	 * （持ってる。持っていない。ほしい。など）
	 * @param state
	 * @return
	 */
	public Set<BookInfo> get(State state) {
		return get(null, state);
	}

	/**
	 * キーワード（title,auther）で検索する。
	 * @param info
	 */
	public Set<BookInfo> get(String keyword, State state) {
		Set<BookInfo> set = new HashSet<>();
		for (Entry<Key, BookInfo> e : map.entrySet()) {
			if (e.getKey().state == state) {
				if (keyword == null || "".equals(keyword)) {
					set.add(e.getValue());

				} else if (e.getValue().getTitleStr().contains(keyword)) {
					set.add(e.getValue());
				} else if (e.getValue().getAuthor().contains(keyword)) {
					set.add(e.getValue());
				}
			}
		}

		return set;

	}

	/**
	 * バッチ。
	 * @param info
	 */
	public void searchNewBook() {

		Set<Tuple> set = new HashSet<>();

		for (Entry<Key, BookInfo> e : map.entrySet()) {
			State state = e.getKey().state;
			if (state == State.HAVE || state == State.WANT) {
				BookInfo info = e.getValue();
				set.add(new Tuple(info.getTitleStr(), info.getAuthor()));
			}

		}

		for (Tuple tuple : set) {
			Set<BookInfo> bookInfos = BookInfoFromWeb
					.getBookInfoFromTitleAuther(tuple.title, tuple.author);
			addBatch(bookInfos);
		}

	}

	public void load(File dir) {
		DirCollector collector = DirCollector.create(dir);
		for (Dir dir2 : collector) {
			for (File f : dir2.fileSet) {
				String ext = getExt(f);
				if ("zip".equals(ext)) {
					Set<String> name = getName(f);

					for (String string : name) {

						if (BookInfo.isBookInfoName(string)) {
							BookInfo bookInfo = BookInfo.createBookInfo(string);
							addHave(bookInfo);

						}
					}
				}
			}

		}

	}

	public Set<String> getName(File file) {
		org.apache.tools.zip.ZipFile zip = null;
		Set<String> set = new HashSet<>();
		try {
			zip = new org.apache.tools.zip.ZipFile(file, "MS932");

			Enumeration<ZipEntry> e = zip.getEntries();

			while (e.hasMoreElements()) {
				ZipEntry ze = e.nextElement();

				if (ze.isDirectory()) {

					set.add(ze.getName().replace("/", ""));
					continue;
				}

			}

		} catch (ZipException e) {

		} catch (IOException e) {

		} finally {
			close(zip);
		}
		return set;
	}

	private void close(ZipFile c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {

			}
		}
	}

	public void load() {
		try {
			Object load = ObjectUtil.load("isbn.data");
			map = (Map<Key, BookInfo>) load;
		} catch (Exception e) {
			log.warn("loadに失敗しました");
		}
	}

	public void save() {
		try {
			ObjectUtil.save("isbn.data", map);

		} catch (Exception e) {
			log.warn("saveに失敗しました");
		}
	}

	class Tuple {
		public Tuple(String title, String author) {
			super();
			this.title = title;
			this.author = author;
		}

		String title;
		String author;

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((author == null) ? 0 : author.hashCode());
			result = prime * result + ((title == null) ? 0 : title.hashCode());
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
			Tuple other = (Tuple) obj;
			if (author == null) {
				if (other.author != null)
					return false;
			} else if (!author.equals(other.author))
				return false;
			if (title == null) {
				if (other.title != null)
					return false;
			} else if (!title.equals(other.title))
				return false;
			return true;
		}
	}

}
