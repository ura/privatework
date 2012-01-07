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
		//repo.searchNewBook();
		//repo.save();

		while (true) {
			String str = ClipBoard.getClipboard();
			for (int i = 0; i < 20; i++) {
				System.out.println();
			}
			{
				Set<BookInfo> set = repo.get(State.HAVE,
						str.split("[ 　\t\\[\\]第]"));
				System.out.println("QUERY*" + str);
				for (BookInfo bookInfo : set) {
					System.out.println("HAVA\t" + bookInfo);
				}
			}
			{
				Set<BookInfo> set = repo.get(State.BAT,
						str.split(" 　\t\\[\\]第"));
				System.out.println("QUERY*" + str);
				for (BookInfo bookInfo : set) {
					System.out.println("BAT\t" + bookInfo);
				}
			}
			{
				Set<BookInfo> set = repo.get(State.WANT,
						str.split(" 　\t\\[\\]第"));
				System.out.println("QUERY*" + str);
				for (BookInfo bookInfo : set) {
					System.out.println("WANT\t" + bookInfo);
				}
			}
		}
	}

}
