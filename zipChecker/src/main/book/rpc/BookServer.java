package book.rpc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;

import org.msgpack.rpc.Server;
import org.msgpack.rpc.loop.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import book.BookInfoRepo;
import book.BookInfoRepo.Key;
import book.webapi.BookInfo;
import conf.ConfConst;

/**
 * ポートは決め打ち。
 * やることはレボジトリの状態をまるごとわたすだけ。
 * @author poti
 *
 */
public class BookServer implements Runnable {

	private static Logger log = LoggerFactory.getLogger(BookServer.class);

	private static final String BOOK_SERVER_IP = ConfConst.MAIN_CONF
			.getVal(ConfConst.BOOK_SERVER_IP);

	private BookInfoRepo bookInfoRepo;

	public BookServer(BookInfoRepo bookInfoRepo) {
		super();
		this.bookInfoRepo = bookInfoRepo;
	}

	public String hello(String msg, int a) {
		System.out.println("server!!!");
		return msg;
	}

	public Collection<BookInfoWeb> getBookInfo() {

		Map<Key, BookInfo> map = bookInfoRepo.getMap();
		Collection<BookInfoWeb> bookInfoWeb = BookInfoWeb.toBookInfoWeb(map);

		return bookInfoWeb;
	}

	@Override
	public void run() {

		if (isServer()) {

			try {
				EventLoop loop = EventLoop.defaultEventLoop();

				log.warn("書籍サーバを立ち上げました。PORT:{}");
				Server svr = new Server();
				svr.serve(this);
				svr.listen(1985);

				loop.join();
			} catch (InterruptedException | IOException e) {
				log.error("書籍サーバ立ち上げ時にエラーが発生しました。", e);
			}
		} else {
			log.error("サーバではなかったので、書籍サーバを立ち上げません。");
		}
	}

	private boolean isServer() {
		boolean result = false;
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			String address = inetAddress.getHostAddress();

			if (BOOK_SERVER_IP.equals(address)) {
				log.warn("サーバ用のIPでした。。IP:{}", address);
				result = true;
			}

		} catch (UnknownHostException e1) {

			log.warn("IPの取得処理でエラー", e1);
		}
		return result;
	}

	public void startThread() {
		new Thread(this).start();
	}

}
