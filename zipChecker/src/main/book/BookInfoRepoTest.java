package book;

import java.io.File;
import java.util.Set;

import org.junit.Test;

import book.BookInfoRepo.State;
import book.webapi.BookInfo;

public class BookInfoRepoTest {
	static BookInfoRepo repo = new BookInfoRepo();

	@org.junit.Before
	public void testLoad() {
		repo.load();
	}

	@Test
	public void testGet() {

		Set<BookInfo> set = repo.get(State.HAVE);
		for (BookInfo bookInfo : set) {
			System.out.println(bookInfo.getInfo());
		}
		System.out.println();
		Set<BookInfo> set2 = repo.get(State.HAVE, "華麗なる食卓");
		for (BookInfo bookInfo : set2) {
			System.out.println(bookInfo.getInfo());
		}
		System.out.println();
		Set<BookInfo> set3 = repo.get(State.HAVE, "華麗");
		for (BookInfo bookInfo : set3) {
			System.out.println(bookInfo.getInfo());
		}

		System.out.println();
		Set<BookInfo> set4 = repo.get(State.HAVE, "食卓");
		for (BookInfo bookInfo : set4) {
			System.out.println(bookInfo.getInfo());
		}

		System.out.println();
		Set<BookInfo> set5 = repo.get(State.HAVE, "ふなつ");
		for (BookInfo bookInfo : set5) {
			System.out.println(bookInfo.getInfo());
		}

	}

	@Test
	public void testLoadFile() {
		repo.load(new File("G:\\完成品"));
	}

	@org.junit.After
	public void testSave() {
		repo.save();
	}

}
