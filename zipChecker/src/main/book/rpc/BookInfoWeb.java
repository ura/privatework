package book.rpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.msgpack.annotation.Message;

import book.BookInfoRepo.Key;
import book.BookInfoRepo.State;
import book.webapi.BookInfo;

@Message
public class BookInfoWeb {

	public String seriesName;
	public String publisherName;
	public String author;
	public String rowTitle;

	public String isbn;
	public boolean rowdateOnly = false;
	public String type;

	public String comment = "";

	public String titleStr;
	public String no;

	public String state;
	public String mapKey;

	public void toBookInfo(Map<Key, BookInfo> map) {

		BookInfo bookInfo = new BookInfo();

		bookInfo.setAuthor(author);
		bookInfo.setComment(comment);
		bookInfo.setIsbn(isbn);
		bookInfo.setNo(no);
		bookInfo.setPublisherName(publisherName);
		bookInfo.setRowdateOnly(rowdateOnly);
		bookInfo.setRowTitle(rowTitle);
		bookInfo.setSeriesName(seriesName);
		bookInfo.setTitleStr(titleStr);
		//TODO TYPEがなしでよいか？

		Key key = new Key(mapKey, State.valueOf(State.class, state));

		map.put(key, bookInfo);

	}

	public static BookInfoWeb toBookInfoWeb(Key key, BookInfo info) {

		BookInfoWeb web = new BookInfoWeb();

		web.seriesName = info.getSeriesName();
		web.publisherName = info.getPublisherName();
		web.author = info.getAuthor();
		web.rowTitle = info.getRowTitle();

		web.isbn = info.getIsbn();
		web.rowdateOnly = info.isRowdateOnly();
		web.type = info.getType().name();
		web.comment = info.getComment();

		web.titleStr = info.getTitleStr();
		web.no = info.getNo();

		web.state = key.state.name();
		web.mapKey = key.isbn;

		return web;

	}

	public static Collection<BookInfoWeb> toBookInfoWeb(Map<Key, BookInfo> map) {

		ArrayList<BookInfoWeb> arrayList = new ArrayList<>();
		for (Entry<Key, BookInfo> e : map.entrySet()) {

			BookInfoWeb infoWeb = toBookInfoWeb(e.getKey(), e.getValue());
			arrayList.add(infoWeb);
		}
		return arrayList;

	}

}
