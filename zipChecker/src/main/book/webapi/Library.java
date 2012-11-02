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

public class Library {

	private static Logger log = LoggerFactory.getLogger(Library.class);

	static abstract class Query {
		int page = 1;

		public abstract void setCustomQuery(AbaronRESTClient stub);

		public void increment() {
			page++;
		}

	}

	public static class IsbnQuery extends Query {

		private String isbn;

		public IsbnQuery(String isbn) {
			this.isbn = isbn;
		}

		@Override
		public void setCustomQuery(AbaronRESTClient stub) {

			stub.setParameter("rft.isbn", isbn);
		}

		@Override
		public String toString() {

			return "ISBN検索:" + isbn;
		}
	}

	private static AbaronResultNode getWebInfo(Query q) {
		AbaronRESTClient stub = new AbaronRESTClient();

		// Amazon WebサービスのエンドポイントURL
		stub.setEndpointUrl("http://iss.ndl.go.jp/books");

		stub.setParameter("search_mode", "advanced");

		q.setCustomQuery(stub);

		// Webサービスを呼び出す
		AbaronResultNode result = stub.doRequest();

		return result;
	}

	private static Document createDoc(String xmlString) throws SAXException,
			IOException, ParserConfigurationException {

		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new CharArrayReader(
				xmlString.toCharArray())));

		return doc;
	}

	/**
	 * 結果が取れなかった場合、NULLを返します。
	 * 結果を取れない原因としては、限定版だとか、古い書籍だとか、
	 * ネットワーク的なエラー等が考えられます。
	 * @param isbn
	 * @return
	 */
	public static BookInfo getInfo(String isbn) {
		SortedSet<BookInfo> set = Library.getInfo(new Library.IsbnQuery(isbn));

		if (set.size() == 1) {
			return set.first();
		} else {
			return null;
		}
	}

	private static SortedSet<BookInfo> getInfo(Query q) {

		SortedSet<BookInfo> set = new TreeSet<BookInfo>();
		AbaronResultNode result = getWebInfo(q);

		try {
			String xmlString = result.getXmlString();
			Document doc = createDoc(xmlString);

			{

				/*
				 <div class="item_summarywrapper">
				  <span></span>

				  <h3>
				          <a href="http://iss.ndl.go.jp/books/R100000002-I000008037110-00">イヴの眠り</a>
				        <span style="font-weight:normal;">5</span>
				      </h3>
				  <p>
				        吉田秋生 著
				        <span style="padding-left:5px;">
				          小学館
				          2006
				          (フラワーコミックス)
				        </span>
				      </p>
				    </div>
				  */

				XPathFactory factory = XPathFactory.newInstance();
				XPath xpath = factory.newXPath();
				XPathExpression expr = xpath
						.compile("//div@class=item_summarywrapper");

				XPathExpression expr2 = xpath.compile("p/text()");
				XPathExpression expr3 = xpath.compile("p/text()");
				XPathExpression expr4 = xpath.compile("p/text()");
				XPathExpression expr5 = xpath.compile("h3/a/text()");
				XPathExpression expr6 = xpath.compile("p/text()");

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

			}

		} catch (XPathExpressionException | ParserConfigurationException
				| SAXException | IOException | RuntimeException e) {

			log.error("検索結果に該当するものがなかったと思われます。{}:{}", q);
			log.info("エラーが発生したXMLを示します。\n{}", result.getXmlString());
		}
		return set;
	}

}
