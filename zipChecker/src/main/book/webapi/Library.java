package book.webapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.SortedSet;
import java.util.TreeSet;

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
import org.w3c.tidy.Tidy;

import util.StringUtil;

public class Library {

	private static Logger log = LoggerFactory.getLogger(Library.class);

	static abstract class Query {

		public abstract void setCustomQuery(QueryBuilder stub);

	}

	public static class IsbnQuery extends Query {

		private String isbn;

		public IsbnQuery(String isbn) {
			this.isbn = isbn;
		}

		@Override
		public void setCustomQuery(QueryBuilder stub) {

			stub.setParameter("rft.isbn", isbn);
		}

		@Override
		public String toString() {

			return "ISBN検索:" + isbn;
		}
	}

	static class QueryBuilder {

		private StringBuilder sb = new StringBuilder();

		public QueryBuilder(String baseQuery) {
			sb.append(baseQuery);

		}

		public void setParameter(String key, String val) {

			sb.append(key).append("=").append(val).append("&");
		}

		@Override
		public String toString() {

			return sb.toString();
		}

	}

	private static Document getWebInfo(Query q) throws IOException {

		QueryBuilder queryBuilder = new QueryBuilder(
				"http://iss.ndl.go.jp/books?");
		queryBuilder.setParameter("search_mode", "advanced");
		q.setCustomQuery(queryBuilder);

		Document doc = null;

		URL url = new URL(queryBuilder.toString());

		URLConnection con = url.openConnection();
		Tidy tidy = new Tidy();

		tidy.setShowWarnings(true);
		tidy.setQuiet(true);

		doc = tidy.parseDOM(
				new BufferedReader(new InputStreamReader(con.getInputStream(),
						"UTF-8")), null);

		if (log.isDebugEnabled()) {
			log.debug(StringUtil.toString(doc));
		}

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
		BookInfo getInfo = _getInfo(isbn);

		if (getInfo != null) {
			return getInfo;

		} else {

			String isbn2 = "";

			if (isbn.length() == 13) {
				isbn2 = ISBNConv.to10From13(isbn);

			} else if (isbn.length() == 10) {
				isbn2 = ISBNConv.to13From10(isbn);

			}
			log.warn("蔵書が見つからなかったのでISBNの変換をしました。{} > {}", new Object[] { isbn,
					isbn2 });

			BookInfo info = _getInfo(isbn2);

			if (info != null) {
				return info;
			} else {
				log.warn("国会図書館にデータがありません。ISBN：{}", isbn);
				return null;
			}

		}
	}

	private static BookInfo _getInfo(String isbn) {
		SortedSet<BookInfo> set = Library.getInfo(new Library.IsbnQuery(isbn));

		if (set.size() == 1) {
			//ISBNをここでSET
			set.first().setIsbn(isbn);
			return set.first();
		} else {

			return null;
		}
	}

	/**
	 * このメソッドで作るISBN値はダミーを突っ込む。
	 * 取得しているHTMLにISBNが含まれないため。
	 * @param q
	 * @return
	 */
	private static SortedSet<BookInfo> getInfo(Query q) {

		SortedSet<BookInfo> set = new TreeSet<BookInfo>();
		Document doc = null;

		try {
			doc = getWebInfo(q);
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
						.compile("//div[@class='item_summarywrapper']");

				XPathExpression authorExp = xpath.compile("p/text()");

				XPathExpression publisherNameexp = xpath
						.compile("p/span/text()");

				XPathExpression tExp = xpath.compile("h3/a/text()");
				XPathExpression tExpKan = xpath.compile("h3/span/text()");

				Object results = expr.evaluate(doc, XPathConstants.NODESET);
				NodeList nodes = (NodeList) results;
				for (int i = 0; i < nodes.getLength(); i++) {

					Node item = nodes.item(i);

					String author = ((String) authorExp.evaluate(item,
							XPathConstants.STRING)).replace("著", "");

					String publisher_year_serises = (String) publisherNameexp
							.evaluate(item, XPathConstants.STRING);

					String[] split = publisher_year_serises
							.split("[\n \\(\\)]");

					String publisherName = split[0];
					String seriesName = split[2];

					String t = (String) tExp.evaluate(item,
							XPathConstants.STRING)
							+ " 第"
							+ getKan((String) tExpKan.evaluate(item,
									XPathConstants.STRING)) + "巻";

					System.out.println((String) tExpKan.evaluate(item,
							XPathConstants.STRING));

					//ISBNがないので、から文字。
					set.add(new BookInfo(publisherName, seriesName, author, t,
							""));

				}

			}

		} catch (XPathExpressionException | IOException | RuntimeException e) {

			log.error("検索結果に該当するものがなかったと思われます。{}:{}", q);
			log.error("", e);
			log.info("エラーが発生したXMLを示します。\n{}", doc);
		}
		return set;
	}

	private static String getKan(String s) {
		String result = s;

		if (s.matches(".*[第巻]+.*")) {
			log.info("巻数に修飾子がついていたので削除します。{}", s);

			result = s.replaceAll("[第巻]", "");
		}

		return result;

	}
}
