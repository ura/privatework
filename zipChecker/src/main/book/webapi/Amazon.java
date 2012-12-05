package book.webapi;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.CopyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import collection.Tuple;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.io.Files;
import com.google.common.io.OutputSupplier;

import conf.ConfConst;
import static util.StaticUtil.sleep;

public class Amazon {

	private static Logger log = LoggerFactory.getLogger(Amazon.class);

	private static final String AWS_ACCESS_KEY_ID = ConfConst.MAIN_CONF
			.getVal(ConfConst.AWS_ACCESS_KEY_ID);

	private static final String AWS_SECRET_KEY = ConfConst.MAIN_CONF
			.getVal(ConfConst.AWS_SECRET_KEY);

	private static final String AssociateTag = ConfConst.MAIN_CONF
			.getVal(ConfConst.AssociateTag);

	private static final String ENDPOINT = "ecs.amazonaws.jp";

	static abstract class Query {
		int page = 1;

		public void increment() {
			page++;
		}

		public abstract void setCustomQuery(Map<String, String> params);

	}

	public static class TitleQuery extends Query {

		private String title;

		public TitleQuery(String t) {
			this.title = t;
		}

		@Override
		public void setCustomQuery(Map<String, String> params) {
			params.put("Operation", "ItemSearch");
			params.put("Keywords", title);
			params.put("SearchIndex", "Books");

			params.put("ItemPage", page + "");

			increment();
		}

		@Override
		public String toString() {

			return "検索ワード　タイトル:" + title;
		}
	}

	public static class TitleAutherQuery extends Query {

		private String title;

		private String author;

		public TitleAutherQuery(String t, String author) {
			this.author = author;
			this.title = t;
		}

		@Override
		public void setCustomQuery(Map<String, String> params) {
			params.put("Operation", "ItemSearch");
			//params.put("Keywords", title + " " + author);
			params.put("Title", title);
			params.put("Author", author);

			params.put("SearchIndex", "Books");

			params.put("ItemPage", page + "");

			increment();

		}

		@Override
		public String toString() {

			return "検索ワード　タイトル:" + title + "   著者:" + author;
		}
	}

	public static class IsbnQuery extends Query {

		private String isbn;

		public IsbnQuery(String isbn) {
			this.isbn = isbn;
		}

		@Override
		public void setCustomQuery(Map<String, String> params) {

			params.put("IdType", "ISBN");
			params.put("ItemId", isbn);
			params.put("SearchIndex", "Books");
			params.put("Operation", "ItemLookup");
			params.put("AssociateTag", AssociateTag);

		}
	}

	public static SortedSet<BookInfo> getInfoByTitle(String title) {
		return Amazon.getInfo(new Amazon.TitleQuery(title));
	}

	public static SortedSet<BookInfo> getInfoByTitleAuther(String title,
			String auther) {
		return Amazon.getInfo(new Amazon.TitleAutherQuery(title, auther));
	}

	//TODO あとで組み込む
	public static SortedSet<BookInfo> validate(SortedSet<BookInfo> set) {
		SortedSet<BookInfo> set2 = new TreeSet<>();
		for (BookInfo bookInfo : set) {
			BookInfo info = getInfo(bookInfo.getIsbn());
			if (info != null) {
				set2.add(info);
			}
		}
		return set2;

	}

	/**
	 * 結果が取れなかった場合、NULLを返します。
	 * 結果を取れない原因としては、限定版だとか、古い書籍だとか、
	 * ネットワーク的なエラー等が考えられます。
	 * @param isbn
	 * @return
	 */
	public static BookInfo getInfo(String isbn) {
		SortedSet<BookInfo> set = Amazon.getInfo(new Amazon.IsbnQuery(isbn));

		if (set.size() == 1) {
			BookInfo info = set.first();
			info.setIsbn(isbn);
			return info;
		} else {
			return null;
		}
	}

	public static String createQuery(Query q) {
		SignedRequestsHelper helper;
		try {

			helper = SignedRequestsHelper.getInstance(ENDPOINT,
					AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);

			Map<String, String> params = new HashMap<String, String>();
			params.put("Service", "AWSECommerceService");
			params.put("Version", "2011-11-31");

			params.put("ResponseGroup", "Small");
			params.put("AssociateTag", "a");

			q.setCustomQuery(params);

			return helper.sign(params);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

	}

	static class GetImage<Void> implements Callable<Void> {

		public GetImage(String asin, String url) {
			super();
			this.asin = asin;
			this.url = url;

		}

		private String asin;
		private String url;

		@Override
		public Void call() throws Exception {

			try (InputStream openStream = new URL(
					"http://www.amazon.co.jp/dp/images/" + asin).openStream();
					BufferedReader br = new BufferedReader(
							new InputStreamReader(openStream, "UTF-8"));) {

				String line;
				StringBuilder sb = new StringBuilder();
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

				String string = sb.toString();
				Pattern compile = Pattern
						.compile("(http://ec2.images-amazon.com/images[^\"]*\\.jpg)\"");
				Matcher matcher = compile.matcher(string);
				if (matcher.find()) {
					log.info("イメージ取得のURLを取得しました。{} : {}", matcher.group(1),
							"http://www.amazon.co.jp/dp/images/" + asin);
					writeImage(new File("image/" + asin + ".jpg"),
							matcher.group(1));
				} else {
					log.info("イメージ取得ができませんでした。{}",
							"http://www.amazon.co.jp/dp/images/" + asin);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

	}

	private static ExecutorService ex = Executors.newFixedThreadPool(20);

	public static void getImage(String asin, String url) {

		GetImage<Void> getImage = new GetImage<Void>(asin, url);
		ex.submit(getImage);

	}

	private static void writeImage(File f, String url) {

		if (!f.exists()) {
			OutputSupplier<FileOutputStream> newOutputStreamSupplier = Files
					.newOutputStreamSupplier(f);
			try (InputStream openStream = new URL(url).openStream();
					BufferedInputStream br = new BufferedInputStream(openStream);
					FileOutputStream output = newOutputStreamSupplier
							.getOutput();) {

				CopyUtils.copy(br, output);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static SortedSet<BookInfo> getInfo(Query q) {
		SortedSet<BookInfo> set = new TreeSet<>();
		String xmlStr = "";
		try {

			String query = createQuery(q);
			log.info("検索クエリ:{}", query);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(query);
			xmlStr = document2String(doc.getDocumentElement());

			NodeList item = doc.getElementsByTagName("Item");

			int record = 10;
			for (int i = 0; i < item.getLength(); i++) {
				try {
					Element elm = (Element) item.item(i);
					NodeList isbnNode = elm.getElementsByTagName("ASIN");
					NodeList titleNode = elm.getElementsByTagName("Title");
					NodeList authorNode = elm.getElementsByTagName("Author");
					NodeList manufacturerNode = elm
							.getElementsByTagName("Manufacturer");
					NodeList detailPageURLNode = elm
							.getElementsByTagName("DetailPageURL");

					String url = detailPageURLNode.item(0).getTextContent();

					//TODO 古い書籍だと、この形ではない・・・・。
					String isbn = ISBNConv.to13From10(isbnNode.item(0)
							.getTextContent());
					getImage(isbnNode.item(0).getTextContent(), url);
					String title = titleNode.item(0).getTextContent();

					if (isbn == null) {
						log.info("書籍ではありません。雑誌等の可能性が高いです。{} {}",
								isbnNode.item(0).getTextContent(), title);
						record--;
						continue;

					}

					String author = authorNode.item(0).getTextContent();
					String pub = manufacturerNode.item(0).getTextContent();
					log.info(title + "  " + author + "  " + pub);

					BookInfo bookInfo = new BookInfo(pub, "", author, title,
							isbn);
					set.add(bookInfo);
				} catch (NullPointerException e) {
					Element elm = (Element) item.item(i);
					record--;
					log.error(
							"想定外のエラーです。タイトルがない、出版社がない等だと思われます。基本無視してください。\n{},\n{}",
							document2String(elm).replaceAll("><", ">\n<"), e);
				}

			}

			paging(q, set, record);

			if (log.isDebugEnabled()) {
				log.debug(document2String(doc.getDocumentElement()).replaceAll(
						"><", ">\n<"));
			}
		} catch (IOException e) {
			log.error("リクエストエラーの可能性があります。リトライを検討します。\n{},\n{}\n{}", xmlStr, e,
					q);
			sleep(120 * 1000l);
			if (e.getMessage().contains("503")) {
				log.error("リクエストエラーの可能性があります。リトライを検討します。\n{},\n{}\n{}", xmlStr,
						e, q);

				q.page--;
				set = getInfo(q);
			}

		} catch (ParserConfigurationException | SAXException | RuntimeException e) {

			log.error("想定外のエラーです。タイトルがない、出版社がない等だと思われます。基本無視してください。\n{},\n{}",
					xmlStr, e);
			log.info("", e);

		}
		return set;
	}

	/**
	 * ページング、および、多量の検索時の条件分岐ロジックです。
	 * @param q
	 * @param set
	 * @param record
	 */
	protected static void paging(Query q, SortedSet<BookInfo> set, int record) {
		if (set.size() == record) {
			log.info("データ件数がMAXに達したので、次ページに移動します。{}", q.page);

			//普通のページング
			if (q.page < 11) {
				SortedSet<BookInfo> info = getInfo(q);

				set.addAll(info);
			} else {
				log.info("ページング限界に達しました。{}", q.page);
			}
			q.page--;

			if (set.size() > 80 && q instanceof TitleQuery && q.page == 1) {
				log.info("データ件数が100件に収まらない可能性があるため検索条件詳細化を検討します。{}", q.page);

				ListMultimap<Tuple<String, String>, BookInfo> map = LinkedListMultimap
						.create();

				for (BookInfo bookInfo : set) {
					map.put(Tuple.newT(bookInfo.getTitleStr(),
							bookInfo.getAuthor()), bookInfo);

				}

				for (Entry<Tuple<String, String>, Collection<BookInfo>> e : map
						.asMap().entrySet()) {

					if (e.getValue().size() > 10) {
						//60巻まで書籍に対応
						Set<String> noset = no_XX(1, 60);
						for (BookInfo info2 : e.getValue()) {
							noset.remove(info2.getNo());
						}

						for (String no : noset) {
							SortedSet<BookInfo> set2 = getInfoByTitleAuther(
									e.getKey().val1 + " " + no, e.getKey().val2);
							set.addAll(set2);

						}

					}

				}

			}
		}
	}

	private static Set<String> no_XX(int x, int y) {

		Set<String> set = new TreeSet<>();
		for (int i = x; i <= y; i++) {
			set.add(no_XX(i));
		}
		return set;
	}

	private static String no_XX(int x) {
		DecimalFormat nf = new DecimalFormat("##");
		nf.setMinimumIntegerDigits(2);
		return nf.format(x);

	}

	public static String document2String(Element elm) {
		String string = null;
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory factory = TransformerFactory.newInstance();

		Transformer former;
		try {
			former = factory.newTransformer();
			former.transform(new DOMSource(elm), result);
			string = result.getWriter().toString();
		} catch (TransformerConfigurationException e) {
			log.error("", e);
		} catch (TransformerException e) {
			log.error("", e);
		}
		return string;
	}
}
