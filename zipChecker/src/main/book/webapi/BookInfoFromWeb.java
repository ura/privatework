package book.webapi;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.WeakHashMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookInfoFromWeb {
	private static Logger log = LoggerFactory.getLogger(BookInfoFromWeb.class);

	static private WeakHashMap<String, SortedSet<BookInfo>> cacheMap1 = new WeakHashMap<String, SortedSet<BookInfo>>();
	static private WeakHashMap<String, SortedSet<BookInfo>> cacheMap2 = new WeakHashMap<String, SortedSet<BookInfo>>();
	static private WeakHashMap<String, SortedSet<BookInfo>> margeMap = new WeakHashMap<String, SortedSet<BookInfo>>();

	public static BookInfo getBookInfoFromTitle(String title, String no) {
		return getBookInfoFromTitle(title, no, false);
	}

	/**
	 * 巻数と、刷数で検索。厳密フラグが立っている場合にはタイトルの完全一致が必要
	 * @param title
	 * @param no
	 * @param restrict
	 * @return
	 */
	public static BookInfo getBookInfoFromTitle(String title, String no,
			boolean restrict) {

		String titleString = title.trim();

		SortedSet<BookInfo> infoByTitle1 = cacheMap1.get(titleString);
		if (infoByTitle1 == null) {
			infoByTitle1 = Rakuten.getInfoByTitle(titleString);
			cacheMap1.put(titleString, infoByTitle1);
		} else {
			log.info("楽天キャッシュより結果を取得しました。{}", titleString);
		}
		SortedSet<BookInfo> infoByTitle2 = cacheMap2.get(titleString);
		if (infoByTitle2 == null) {
			infoByTitle2 = Amazon.getInfoByTitle(titleString);
			cacheMap2.put(titleString, infoByTitle2);
		} else {
			log.info("Amazonキャッシュより結果を取得しました。{}", titleString);
		}

		SortedSet<BookInfo> margeSet = margeMap.get(titleString);
		if (margeSet == null) {
			margeSet = new TreeSet<>();
			Map<String, BookInfo> m1 = new HashMap<String, BookInfo>();
			for (BookInfo bookInfo : infoByTitle1) {
				m1.put(bookInfo.getIsbn(), bookInfo);
			}
			for (BookInfo bookInfo : infoByTitle2) {

				//検索性能が違う可能性があるため。
				BookInfo bookInfo2 = m1.get(bookInfo.getIsbn());
				if (bookInfo2 != null) {
					bookInfo2 = Rakuten.getInfo(bookInfo.getIsbn());
				}
				margeSet.add(marge(bookInfo, bookInfo2));

				m1.remove(bookInfo.getIsbn());
			}
			//楽天の残りかす
			for (BookInfo bookInfo : m1.values()) {
				margeSet.add(marge(bookInfo, Amazon.getInfo(bookInfo.getIsbn())));
			}

			margeMap.put(titleString, margeSet);

		}

		return getBookInfoByNo(title, no, restrict, margeSet);

	}

	protected static BookInfo getBookInfoByNo(String title, String no,
			boolean restrict, SortedSet<BookInfo> margeSet) {
		SortedSet<BookInfo> result = new TreeSet<>();
		for (BookInfo bookInfo : margeSet) {
			if (bookInfo.getNo().equals(no)) {

				if (restrict) {

					if (bookInfo.getTitleStr().equals(title)) {
						result.add(bookInfo);
					}
				} else {
					result.add(bookInfo);
				}
				log.info(bookInfo.getInfo());
			}
		}

		if (result.size() == 1) {
			return result.first();
		} else {
			Set<String> set = new HashSet<>();
			for (BookInfo bookInfo : result) {
				log.warn("タイトル検索で複数結果が検出されました。{}", bookInfo.getInfo());
				set.add(bookInfo.getBaseInfo());
			}
			if (set.size() == 1) {
				log.warn("基礎情報は同一だったため、代表して返します。。{}", result.first().getInfo());
				return result.first();
			}

			return null;
		}
	}

	public static BookInfo getBookInfoFromTitleAuther(String title,
			String auther, String no, boolean restrict) {

		SortedSet<BookInfo> bookInfoFromTitleAuther = getBookInfoFromTitleAuther(
				title, auther);
		return getBookInfoByNo(title, no, restrict, bookInfoFromTitleAuther);

	}

	public static SortedSet<BookInfo> getBookInfoFromTitleAuther(String title,
			String auther) {

		String titleString = title.trim();

		SortedSet<BookInfo> infoByTitle1 = cacheMap1.get(titleString + "-"
				+ auther);
		if (infoByTitle1 == null) {
			infoByTitle1 = Rakuten.getInfoByTitleAuther(titleString, auther);
			cacheMap1.put(titleString + "-" + auther, infoByTitle1);
		} else {
			log.info("楽天キャッシュより結果を取得しました。{}", titleString + "-" + auther);
		}
		SortedSet<BookInfo> infoByTitle2 = cacheMap2.get(titleString + "-"
				+ auther);
		if (infoByTitle2 == null) {
			infoByTitle2 = Amazon.getInfoByTitleAuther(titleString, auther);
			cacheMap2.put(titleString + "-" + auther, infoByTitle2);
		} else {
			log.info("Amazonキャッシュより結果を取得しました。{}", titleString + "-" + auther);
		}

		SortedSet<BookInfo> margeSet = margeMap.get(titleString + "-" + auther);
		if (margeSet == null) {
			margeSet = new TreeSet<>();
			Map<String, BookInfo> m1 = new HashMap<String, BookInfo>();
			for (BookInfo bookInfo : infoByTitle1) {
				m1.put(bookInfo.getIsbn(), bookInfo);
			}
			for (BookInfo bookInfo : infoByTitle2) {

				//検索性能が違う可能性があるため。
				BookInfo bookInfo2 = m1.get(bookInfo.getIsbn());
				if (bookInfo2 != null) {
					bookInfo2 = Rakuten.getInfo(bookInfo.getIsbn());
				}
				margeSet.add(marge(bookInfo, bookInfo2));

				m1.remove(bookInfo.getIsbn());
			}
			//楽天の残りかす
			for (BookInfo bookInfo : m1.values()) {
				margeSet.add(marge(bookInfo, Amazon.getInfo(bookInfo.getIsbn())));
			}

			margeMap.put(titleString + "-" + auther, margeSet);

		}

		return margeSet;
	}

	public static BookInfo getBookInfo(String isbn) {

		BookInfo info0 = Library.getInfo(isbn);
		BookInfo info1 = Amazon.getInfo(isbn);
		BookInfo info2 = Rakuten.getInfo(isbn);

		LinkedHashMap<String, BookInfo> map = new LinkedHashMap<>();
		map.put("国会", info0);
		map.put("aws", info1);
		map.put("楽天", info2);

		BookInfo info = marge(map);

		return info;
	}

	private static void filterNull(LinkedHashMap<String, BookInfo> map) {

		for (String site : map.keySet().toArray(new String[0])) {

			if (map.get(site) == null) {
				map.remove(site);

			}

		}

	}

	/**
	 * MAPの中のBEANで一番短い名前で更新する。
	 * @param r
	 * @param map
	 * @param name
	 */
	private static void setShorName(BookInfo r,
			LinkedHashMap<String, BookInfo> map, String name) {

		for (BookInfo i : map.values()) {

			try {
				String base = (String) PropertyUtils.getProperty(r, name);
				String nextVal = (String) PropertyUtils.getProperty(i, name);

				if (base.length() > nextVal.length()) {

					PropertyUtils.setProperty(r, name, nextVal);
				}

			} catch (IllegalAccessException | InvocationTargetException
					| NoSuchMethodException e) {

				throw new IllegalStateException(name, e);

			}

		}

	}

	private static void setShorName(BookInfo r,
			LinkedHashMap<String, BookInfo> map) {

		String[] names = { "titleStr", "author", "publisherName" };

		for (String n : names) {
			setShorName(r, map, n);

		}

	}

	/**
	 *
	 * @param b1
	 * @param b2
	 * @return
	 */
	private static BookInfo marge(BookInfo b1, BookInfo b2) {
		LinkedHashMap<String, BookInfo> map = new LinkedHashMap<>();
		map.put("aws", b1);
		map.put("楽天", b2);

		return marge(map);

	}

	private static BookInfo marge(LinkedHashMap<String, BookInfo> map) {

		filterNull(map);

		SortedSet<BookInfo> set = new TreeSet<>();
		for (BookInfo info : map.values()) {
			set.add(info);
		}

		if (set.size() == 1) {
			return set.first();
		} else {

			for (Entry<String, BookInfo> e : map.entrySet()) {

				log.warn("書籍情報に差異があります。{} : {}", e.getKey(), e.getValue()
						.getInfo());

			}

			BookInfo r = null;

			if (set.size() > 1) {
				r = set.first();
				setShorName(r, map);
				log.warn("書籍情報を更新しました。　　　  {}", r.getInfo());
			}

			return r;

		}

	}
}
