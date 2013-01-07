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
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Normalizer;
import util.file.Dir;
import util.file.DirCollector;
import util.file.ObjectUtil;
import book.rpc.BookClient;
import book.webapi.BookInfo;
import book.webapi.BookInfoFromWeb;
import book.webapi.ISBNConv;
import static util.file.FileNameUtil.getExt;
import static util.file.FileNameUtil.getFileName;

/**
 *
 * TODO 検索のまとめ機能を作る
 * TODO 不正規データの登録を検討する。
 *
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

		HAVE, WANT,
		/**
		 * 新刊探査の結果キー
		 */
		BAT, NEEDLESS,
		/**
		 * 検索用ダミーキー
		 */
		DUMMY, ANY, FOLDERNAME
	};

	public BookInfoRepo() {
		load();
	}

	/**
	 *
	 * ISBNが取得できていない書籍に関しては、フォルダ名がキーとなる
	 *
	 * @author poti
	 *
	 */

	public static class Key implements Serializable {
		public Key(String isbn, State state) {
			super();
			this.isbn = isbn;
			this.state = state;
		}

		/**
		 *
		 */
		private static final long serialVersionUID = 4935627485713044172L;
		public String isbn;
		public State state;

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

	/**
	 * 所持している本を登録する
	 * @param info
	 */
	public void addHave(BookInfo info) {
		if (info.isRowdateOnly()) {
			map.put(new Key(info.getIsbn(), State.FOLDERNAME), info);
		} else {
			map.put(new Key(info.getIsbn(), State.HAVE), info);
		}
	}

	/**
	 * 持っていない候補を突っ込む。
	 * @param info
	 */
	private synchronized void addBatch(Set<BookInfo> bookInfos) {
		for (BookInfo info : bookInfos) {
			//書籍情報は編集されている可能性があるため。ISBNで検索する
			if (!map.containsKey(new Key(info.getIsbn(), State.DUMMY))) {

				if (getByTitle_Author_No(State.ANY, info.getRowTitle(),
						info.getAuthor(), info.getNo()).size() == 0) {
					map.put(new Key(info.getIsbn(), State.BAT), info);
				} else {
					log.warn("類似の書籍情報が存在していたため、登録しませんでした。{}", info.getInfo());
				}

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
	 * 不正なISBNを補正する。
	 * @return
	 */
	public void repalreISBN() {
		Set<BookInfo> result = new HashSet<>();

		Set<BookInfo> set = get(State.ANY);

		for (BookInfo bookInfo : set) {

			//フォルダのやつは無視する。
			if (!bookInfo.isRowdateOnly()) {

				if (!ISBNConv.check(bookInfo.getIsbn())) {
					result.add(bookInfo);
					log.warn("不正な書籍情報です。:{}", bookInfo.getInfo());
				}
			}
		}

		for (BookInfo bookInfo : result) {
			remove(bookInfo);
			bookInfo.repalreISBN();
			addHave(bookInfo);

		}

	}

	/**
	 * 状態を指定して一覧を出す。
	 * （持ってる。持っていない。ほしい。など）
	 * @param state
	 * @return
	 */
	public Set<BookInfo> get(State state) {
		return get(state, null);
	}

	public Set<BookInfo> getByTitle_Author_No(State state, String t, String a,
			String n) {
		Set<BookInfo> set = get(state, t, a);
		Set<BookInfo> result = new HashSet<>();

		for (BookInfo bookInfo : set) {

			if (bookInfo.getTitleStr().equals(t)
					&& bookInfo.getAuthor().equals(a)
					&& bookInfo.getNo().equals(n)) {
				result.add(bookInfo);
			}

		}

		return result;

	}

	/**
	 * キーワード（title,auther）で検索する。
	 * 巻数は対象外。
	 * @param info
	 */
	public Set<BookInfo> get(State state, String... keywords) {
		Set<BookInfo> set = new TreeSet<BookInfo>();
		log.info("{}:{}:{}:{}", keywords);
		for (Entry<Key, BookInfo> e : map.entrySet()) {
			if (state == State.ANY || e.getKey().state == state) {

				if (keywords != null) {
					boolean flag = true;

					for (String keyword : keywords) {

						if (Normalizer.contain(e.getValue().getTitleStr(),
								keyword)) {
							continue;
						}

						if (Normalizer.contain(e.getValue().getAuthor(),
								keyword)) {
							continue;
						}
						if (Normalizer.contain(e.getValue().getNo(), keyword)) {
							continue;
						}
						flag = false;
						break;

					}

					if (flag) {
						set.add(e.getValue());
					}
				} else {
					set.add(e.getValue());
				}

			}
		}

		return set;

	}

	/**
	 * 新刊情報を検索する。
	 * 具体的には、
	 * 持っている本の「タイトル」「著者名」で検索し
	 * @param info
	 */
	public void searchNewBook() {

		Set<Tuple> set = new HashSet<>();
		removeSearchInfo();
		for (Entry<Key, BookInfo> e : map.entrySet()) {
			State state = e.getKey().state;
			if (state == State.HAVE || state == State.WANT) {
				BookInfo info = e.getValue();
				set.add(new Tuple(info.getTitleStr(), info.getAuthor()));
			}

		}

		//作ったけど、無駄に。1秒に1件のみらしい。
		ThreadPoolExecutor ex = new ThreadPoolExecutor(1, 1, 0L,
				TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

		for (Tuple tuple : set) {

			final Tuple t = tuple;
			ex.submit(new Runnable() {

				@Override
				public void run() {
					Set<BookInfo> bookInfos = BookInfoFromWeb
							.getBookInfoFromTitleAuther(t.title, t.author);
					addBatch(bookInfos);

				}

			});

		}

	}

	public void remove(BookInfo info) {
		map.remove(new Key(info.getIsbn(), State.DUMMY));

	}

	/**
	 * バッチ。
	 * @param info
	 */
	public void removeSearchInfo() {

		Set<Key> set = new HashSet<>();

		for (Entry<Key, BookInfo> e : map.entrySet()) {
			State state = e.getKey().state;
			if (state == State.BAT) {

				set.add(e.getKey());
			}

		}
		for (Key key : set) {
			map.remove(key);
		}

	}

	public void load(File dir) {
		DirCollector collector = DirCollector.create(dir);
		for (Dir dir2 : collector) {
			for (File f : dir2.fileSet) {
				String ext = getExt(f);
				if ("zip".equals(ext)) {

					//一冊用のロジック
					if (BookInfo.isBookInfoName(getFileName(f))) {
						BookInfo bookInfo = BookInfo
								.createBookInfo(getFileName(f));
						addHave(bookInfo);
					}

					Set<String> name = getNameFromZip(f);

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

	/**
	 * ZIP内のエントリ、フォルダ名を抜き取る。
	 * @param file
	 * @return
	 */
	public Set<String> getNameFromZip(File file) {
		org.apache.tools.zip.ZipFile zip = null;
		Set<String> set = new HashSet<>();
		try {
			zip = new org.apache.tools.zip.ZipFile(file, "MS932");

			@SuppressWarnings("unchecked")
			Enumeration<ZipEntry> e = zip.getEntries();

			while (e.hasMoreElements()) {
				ZipEntry ze = e.nextElement();

				if (ze.isDirectory()) {

					String name = ze.getName();
					//２重ディレクトリになっているエントリは無視する。
					if (!name.substring(0, name.length() - 1).contains("/")) {

						set.add(ze.getName().replace("/", ""));
					}
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
			Object load = ObjectUtil.load("isbn.data", Map.class);
			map = (Map<Key, BookInfo>) load;

			repalreISBN();

			save();

			log.warn("ローカルデータ　LOAD完了 データ保持数:{}", map.size());

		} catch (Exception e) {
			e.printStackTrace();
			log.warn("書籍ローカルデータのloadに失敗しました");
		}

		BookClient bookClient = new BookClient();
		if (bookClient.isClient()) {

			log.warn("書籍サーバよりデータをロードします");

			Map<Key, BookInfo> dataFromServer = bookClient.getDataFromServer();
			log.warn("書籍サーバよりデータをロードしました");

			if (map == null) {
				map = dataFromServer;
			} else {
				log.warn("書籍サーバのデータをマージしました。");
				map.putAll(dataFromServer);

			}
			save();

		} else {
			log.warn("サーバのIPだったため、データの取得をしませんでした。");
		}

	}

	public void save() {
		try {
			ObjectUtil.save("isbn.data", map);

			BookClient bookClient = new BookClient();
			if (bookClient.isClient()) {
				log.warn("サーバにデータをSAVEします。");
				bookClient.saveToServer(this);

			}

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

	public Map<Key, BookInfo> getMap() {
		return map;
	}

}
