package book;

import java.io.Serializable;
import java.util.Set;

import util.ClipBoard;
import book.BookInfoRepo.State;
import book.webapi.BookInfo;

/**
 * 新刊の探査をつける。
 * ステータスをつける。
 * @author name
 *
 */
public class BookInfoRepoCUI implements Serializable {

	public static void main(String[] args) {
		BookInfoRepo repo = new BookInfoRepo();

		repo.load();

		while (true) {
			String str = ClipBoard.getClipboard();

			Set<BookInfo> set = repo.get(State.HAVE, str.split(" "));
			for (BookInfo bookInfo : set) {
				System.out.println(bookInfo);
			}
		}
	}

}
