package book.webapi;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookInfoFromWeb {
	private static Logger log = LoggerFactory.getLogger(BookInfoFromWeb.class);

	static private WeakHashMap<String, SortedSet<BookInfo>> cacheMap1 = new WeakHashMap<String, SortedSet<BookInfo>>();
	static private WeakHashMap<String, SortedSet<BookInfo>> cacheMap2 = new WeakHashMap<String, SortedSet<BookInfo>>();
	static private WeakHashMap<String, SortedSet<BookInfo>> margeMap = new WeakHashMap<String, SortedSet<BookInfo>>();

	public static BookInfo getBookInfoFromTitle(String title, String no) {

		String titleString = title.trim();

		SortedSet<BookInfo> infoByTitle1 = cacheMap1.get(titleString);
		if (infoByTitle1 == null) {
			infoByTitle1 = Rakuten.getInfoByTitle(titleString);
			cacheMap1.put(titleString, infoByTitle1);
		} else {
			log.info("楽天キャッシュより結果を取得しました。{}", titleString);
		}
		SortedSet<BookInfo> infoByTitle2 = cacheMap1.get(titleString);
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

		SortedSet<BookInfo> result = new TreeSet<>();
		for (BookInfo bookInfo : margeSet) {
			if (bookInfo.getNo().equals(no)) {

				result.add(bookInfo);
				log.info(bookInfo.getInfo());
			}
		}

		if (result.size() == 1) {
			return result.first();
		} else {
			for (BookInfo bookInfo : result) {
				log.warn("タイトル検索で複数結果が検出されました。{}", bookInfo.getInfo());
			}

			return null;
		}

	}

	public static Set<BookInfo> getBookInfoFromTitleAuther(String title,
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
		SortedSet<BookInfo> infoByTitle2 = cacheMap1.get(titleString + "-"
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

		BookInfo info1 = Amazon.getInfo(isbn);
		BookInfo info2 = Rakuten.getInfo(isbn);

		BookInfo info = marge(info1, info2);

		return info;
	}

	private static BookInfo marge(BookInfo b1, BookInfo b2) {

		if (b1 == null) {
			return b2;
		}
		if (b2 == null) {
			return b1;
		}

		if (b1.getInfo().equals(b2.getInfo())) {
			return b1;
		} else {
			log.warn("書籍情報に差異があります。AWS   {}", b1.getInfo());
			log.warn("書籍情報に差異があります。楽天  {}", b2.getInfo());

			BookInfo r = b1;

			if (b1.getTitle().length() > b2.getTitle().length()) {
				r.setTitleStr(b2.getTitleStr());
			} else {
				r.setTitleStr(b1.getTitleStr());
			}

			if (b1.getAuthor().length() > b2.getAuthor().length()) {
				r.setAuthor(b2.getAuthor());
			} else {
				r.setAuthor(b1.getAuthor());
			}

			if (b1.getPublisherName().length() > b2.getPublisherName().length()) {
				r.setPublisherName(b2.getPublisherName());
			} else {
				r.setPublisherName(b1.getPublisherName());
			}

			log.warn("書籍情報を更新しました。　　　  {}", r.getInfo());

			return r;

		}

	}
}
