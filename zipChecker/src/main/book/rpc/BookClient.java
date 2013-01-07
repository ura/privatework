package book.rpc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.msgpack.rpc.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import book.BookInfoRepo;
import book.BookInfoRepo.Key;
import book.webapi.BookInfo;
import conf.ConfConst;

public class BookClient {
	private static Logger log = LoggerFactory.getLogger(BookClient.class);
	private static final String BOOK_SERVER_IP = ConfConst.MAIN_CONF
			.getVal(ConfConst.BOOK_SERVER_IP);

	public interface BookServerInterface {

		Collection<BookInfoWeb> getBookInfo();

		void setBookInfo(Collection<BookInfoWeb> infos);

	}

	public boolean isClient() {
		boolean result = false;
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			String address = inetAddress.getHostAddress();

			if (!BOOK_SERVER_IP.equals(address)) {
				log.warn("サーバのIPではありませんでした。。IP:{}", address);
				result = true;
			}

		} catch (UnknownHostException e1) {

			log.warn("IPの取得処理でエラー", e1);
		}
		return result;
	}

	/**
	 * サーバにデータをSAVEします
	 * @return
	 */
	public void saveToServer(BookInfoRepo bookInfoRepo) {
		Map<Key, BookInfo> m = new HashMap<BookInfoRepo.Key, BookInfo>();

		try {

			log.info("client call");

			Client cli = new Client(BOOK_SERVER_IP, 1985);
			BookServerInterface iface = cli.proxy(BookServerInterface.class);

			Map<Key, BookInfo> map = bookInfoRepo.getMap();

			Collection<BookInfoWeb> bookInfoWeb = BookInfoWeb
					.toBookInfoWeb(map);

			iface.setBookInfo(bookInfoWeb);

			cli.close();
			log.info("server connection close!");
		} catch (Exception e) {
			log.error("リモートサーバーへのSAVEに失敗しました。", e);
		}

	}

	public Map<Key, BookInfo> getDataFromServer() {
		Map<Key, BookInfo> m = new HashMap<BookInfoRepo.Key, BookInfo>();

		try {

			log.info("client call");

			Client cli = new Client(BOOK_SERVER_IP, 1985);
			BookServerInterface iface = cli.proxy(BookServerInterface.class);

			System.out.println(iface.getBookInfo().size());

			Collection<BookInfoWeb> bookInfo = iface.getBookInfo();

			log.warn("Get Data!! size:{}", bookInfo.size());
			for (BookInfoWeb bookInfoWeb : bookInfo) {
				bookInfoWeb.toBookInfo(m);

			}
			cli.close();
			log.info("server connection close!1");
		} catch (Exception e) {
			log.error("リモートサーバーからのデータ取得に失敗しました。", e);
		}

		return m;

	}
}
