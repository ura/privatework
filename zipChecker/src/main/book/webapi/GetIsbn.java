package book.webapi;

import java.io.File;
import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.file.Dir;
import util.file.DirCollector;
import util.file.FileWalker;
import util.file.filter.DirFilter;
import static util.file.FileNameUtil.createPath;

/**
 * フォルダ名からISBNを取得できるようにしたい・・・
 * @author name
 *
 */
public class GetIsbn {
	private static Logger log = LoggerFactory.getLogger(GetIsbn.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		hoge(new File("G:\\arkwork2"));
		hoge(new File("G:\\arkwork"));

	}

	private static void hoge(File dir) {

		File[] listFiles = dir.listFiles(new DirFilter());

		DirCollector dircoll = new DirCollector();
		new FileWalker().walk(dir, dircoll);

		for (Dir d : dircoll) {
			if (BookInfo.isBookInfoName(d.dir)) {
				BookInfo bookInfo = BookInfo.createBookInfo(d.dir);

				if (!bookInfo.isTrueISBN()) {

					log.warn("ISBNが不正なフォルダを見つけました。{}", d.dir.getAbsolutePath());

					SortedSet<BookInfo> infoByTitle = Rakuten
							.getInfoByTitle(bookInfo.getTitleStr() + " "
									+ bookInfo.getNo());

					if (infoByTitle.size() == 1) {
						log.warn("ISBNの更新を行います。{}", d.dir.getAbsolutePath());
						log.warn("更新前。{}", bookInfo.getInfo());
						bookInfo.setIsbn(infoByTitle.first().getIsbn());
						log.warn("更新後。{}", bookInfo.getInfo());

						boolean renameTo = d.dir.renameTo(createPath(
								d.dir.getParent(), bookInfo.getInfo()));
						if (!renameTo) {
							throw new IllegalStateException();
						}

					} else {
						log.warn("タイトルが不適切なため、目的の候補が得られませんでした。{}",
								infoByTitle.size());
						for (BookInfo bookInfo2 : infoByTitle) {
							log.warn("候補を列挙します。{}", bookInfo2.getInfo());
						}

					}
				}
			}

		}

	}
}
