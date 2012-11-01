package book.webapi;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.web2driver.abaron.client.AbaronRESTClient;
import com.web2driver.abaron.client.AbaronResultNode;

import conf.ConfConst;

public class Rakuten {

	private static Logger log = LoggerFactory.getLogger(Rakuten.class);

	private static final String KEY = ConfConst.MAIN_CONF
			.getVal(ConfConst.RAKUTEN_KEY);

	static abstract class Query {
		int page = 1;

		public abstract void setCustomQuery(AbaronRESTClient stub);

		public void increment() {
			page++;
		}

	}

	public static class TitleQuery extends Query {

		private String title;

		public TitleQuery(String t) {
			this.title = t;
		}

		@Override
		public void setCustomQuery(AbaronRESTClient stub) {

			stub.setParameter("title", title);
		}

		@Override
		public String toString() {

			return "タイトル検索:" + title;
		}
	}

	public static class TitleAuthorQuery extends Query {

		private String title;
		private String author;

		public TitleAuthorQuery(String t, String author) {
			this.author = author;
			this.title = t;
		}

		@Override
		public void setCustomQuery(AbaronRESTClient stub) {

			stub.setParameter("title", title);
			stub.setParameter("author", author);
		}

		@Override
		public String toString() {

			return "タイトル検索:" + title + "著者検索:" + author;
		}
	}

	public static class IsbnQuery extends Query {

		private String isbn;

		public IsbnQuery(String isbn) {
			this.isbn = isbn;
		}

		@Override
		public void setCustomQuery(AbaronRESTClient stub) {

			stub.setParameter("isbn", isbn);
		}

		@Override
		public String toString() {

			return "ISBN検索:" + isbn;
		}
	}

	private static AbaronResultNode getWebInfo(Query q) {
		AbaronRESTClient stub = new AbaronRESTClient();

		// Amazon WebサービスのエンドポイントURL
		stub.setEndpointUrl("http://api.rakuten.co.jp/rws/3.0/rest");

		// URLパラメータを設定する
		stub.setParameter("developerId", KEY);
		stub.setParameter("operation", "BooksBookSearch");
		stub.setParameter("version", "2011-12-01");

		stub.setParameter("Operation", "ItemLookup");
		stub.setParameter("ResponseGroup", "Small");
		stub.setParameter("booksGenreID", "000");
		stub.setParameter("size", "9");
		stub.setParameter("page", q.page);

		q.increment();

		q.setCustomQuery(stub);

		// Webサービスを呼び出す
		AbaronResultNode result = stub.doRequest();

		return result;
	}

	private static Document createDoc(String xmlString) throws SAXException,
			IOException, ParserConfigurationException {

		xmlString = xmlString.substring(xmlString.indexOf("<Body>"),
				xmlString.indexOf("</Body>") + "</Body>".length());

		xmlString = xmlString.replace(":BooksBookSearch", "");

		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new CharArrayReader(
				xmlString.toCharArray())));

		return doc;
	}

	public static SortedSet<BookInfo> getInfoByTitle(String title) {
		return Rakuten.getInfo(new Rakuten.TitleQuery(title));
	}

	public static SortedSet<BookInfo> getInfoByTitleAuther(String title,
			String author) {
		return Rakuten.getInfo(new Rakuten.TitleAuthorQuery(title, author));
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
		SortedSet<BookInfo> set = Rakuten.getInfo(new Rakuten.IsbnQuery(isbn));

		if (set.size() == 1) {
			return set.first();
		} else {
			return null;
		}
	}

	public static SortedSet<BookInfo> getInfo(Query q) {

		SortedSet<BookInfo> set = new TreeSet<BookInfo>();
		AbaronResultNode result = getWebInfo(q);

		try {
			String xmlString = result.getXmlString();
			Document doc = createDoc(xmlString);

			{
				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();
				XPathExpression expr = xpath.compile("//Item");

				XPathExpression expr2 = xpath.compile("seriesName/text()");
				XPathExpression expr3 = xpath.compile("author/text()");
				XPathExpression expr4 = xpath.compile("publisherName/text()");
				XPathExpression expr5 = xpath.compile("title/text()");
				XPathExpression expr6 = xpath.compile("isbn/text()");

				Object results = expr.evaluate(doc, XPathConstants.NODESET);
				NodeList nodes = (NodeList) results;
				for (int i = 0; i < nodes.getLength(); i++) {

					Node item = nodes.item(i);

					String seriesName = (String) expr2.evaluate(item,
							XPathConstants.STRING);
					String author = (String) expr3.evaluate(item,
							XPathConstants.STRING);
					String publisherName = (String) expr4.evaluate(item,
							XPathConstants.STRING);
					String t = (String) expr5.evaluate(item,
							XPathConstants.STRING);
					String isbn = (String) expr6.evaluate(item,
							XPathConstants.STRING);

					set.add(new BookInfo(publisherName, seriesName, author, t,
							isbn));

				}
				if (set.size() == 30) {
					log.info("データ件数がMAXに達したので、次ページに移動します。", q.page);
					SortedSet<BookInfo> info = getInfo(q);
					set.addAll(info);
				}
			}

		} catch (XPathExpressionException | ParserConfigurationException
				| SAXException | IOException | RuntimeException e) {

			log.error("検索結果に該当するものがなかったと思われます。{}:{}", q);
			log.info("エラーが発生したXMLを示します。\n{}", result.getXmlString());
		}
		return set;
	}

	public static SortedSet<String> getAuthor(Query q) {

		SortedSet<String> set = new TreeSet<String>();
		AbaronResultNode result = getWebInfo(q);

		// result.print(false);

		try {
			String xmlString = result.getXmlString();
			Document doc = createDoc(xmlString);

			{
				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();
				XPathExpression expr = xpath.compile("//Item/author/text()");

				Object results = expr.evaluate(doc, XPathConstants.NODESET);
				NodeList nodes = (NodeList) results;
				for (int i = 0; i < nodes.getLength(); i++) {
					// System.out.println(nodes.item(i).getNodeValue());
					set.add(nodes.item(i).getNodeValue());
				}
			}

		} catch (XPathExpressionException | ParserConfigurationException
				| SAXException | IOException | RuntimeException e) {

			log.error("想定外のエラー", e);
			result.print(false);
		}

		return set;
	}
}
