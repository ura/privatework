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
import java.util.HashMap;
import java.util.Map;
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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.io.Files;
import com.google.common.io.OutputSupplier;

import conf.ConfConst;

public class Amazon {

	private static Logger log = LoggerFactory.getLogger(Amazon.class);

	private static final String AWS_ACCESS_KEY_ID = ConfConst.MAIN_CONF
			.getVal(ConfConst.AWS_ACCESS_KEY_ID);

	private static final String AWS_SECRET_KEY = ConfConst.MAIN_CONF
			.getVal(ConfConst.AWS_SECRET_KEY);

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
			params.put("Keywords", title + " " + author);
			params.put("SearchIndex", "Books");

			params.put("ItemPage", page + "");

			increment();

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
		}
	}

	public static SortedSet<BookInfo> getInfoByTitle(String title) {
		return Amazon.getInfo(new Amazon.TitleQuery(title));
	}

	public static SortedSet<BookInfo> getInfoByTitleAuther(String title,
			String auther) {
		return Amazon.getInfo(new Amazon.TitleAutherQuery(title, auther));
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

	private static ExecutorService ex = Executors.newFixedThreadPool(3);

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
			xmlStr = document2String(doc);
			NodeList isbnNode = doc.getElementsByTagName("ASIN");
			NodeList titleNode = doc.getElementsByTagName("Title");
			NodeList authorNode = doc.getElementsByTagName("Author");
			NodeList manufacturerNode = doc
					.getElementsByTagName("Manufacturer");
			NodeList detailPageURLNode = doc
					.getElementsByTagName("DetailPageURL");

			for (int i = 0; i < titleNode.getLength(); i++) {

				String url = detailPageURLNode.item(i).getTextContent();

				//TODO 古い書籍だと、この形ではない・・・・。
				String isbn = "978" + isbnNode.item(i).getTextContent();
				getImage(isbnNode.item(i).getTextContent(), url);

				String title = titleNode.item(i).getTextContent();
				String author = authorNode.item(i).getTextContent();
				String pub = manufacturerNode.item(i).getTextContent();
				log.info(title + "  " + author + "  " + pub);

				BookInfo bookInfo = new BookInfo(pub, "", author, title, isbn);
				set.add(bookInfo);

			}

			if (set.size() == 10) {
				log.info("データ件数がMAXに達したので、次ページに移動します。", q.page);
				SortedSet<BookInfo> info = getInfo(q);
				set.addAll(info);
			}

			if (log.isDebugEnabled()) {
				log.debug(document2String(doc).replaceAll("><", ">\n<"));
			}
		} catch (ParserConfigurationException | SAXException | IOException
				| RuntimeException e) {

			log.error("想定外のエラーです。タイトルがない、出版社がない等だと思われます。基本無視してください。\n{},\n{}",
					xmlStr, e);
			log.info("", e);

		}
		return set;
	}

	public static String document2String(Document doc) {
		String string = null;
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory factory = TransformerFactory.newInstance();

		Transformer former;
		try {
			former = factory.newTransformer();
			former.transform(new DOMSource(doc.getDocumentElement()), result);
			string = result.getWriter().toString();
		} catch (TransformerConfigurationException e) {
			log.error("", e);
		} catch (TransformerException e) {
			log.error("", e);
		}
		return string;
	}
}
