package webapi;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import conf.ConfConst;

public class Amazon {

	private static Logger log = LoggerFactory.getLogger(Amazon.class);

	private static final String AWS_ACCESS_KEY_ID = ConfConst.MAIN_CONF
			.getVal(ConfConst.AWS_ACCESS_KEY_ID);

	private static final String AWS_SECRET_KEY = ConfConst.MAIN_CONF
			.getVal(ConfConst.AWS_SECRET_KEY);

	private static final String ENDPOINT = "ecs.amazonaws.jp";

	static abstract class Query {
		public abstract void setCustomQuery(Map<String, String> params);

	}

	public static class TitleQuery extends Query {

		private String title;

		public TitleQuery(String t) {
			this.title = t;
		}

		@Override
		public void setCustomQuery(Map<String, String> params) {
			params.put("title", title);
			throw new IllegalStateException("未実装");

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
		}
	}

	public static SortedSet<BookInfo> getInfoByTitle(String title) {
		return Amazon.getInfo(new Amazon.TitleQuery(title));
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
			params.put("Operation", "ItemLookup");

			params.put("ResponseGroup", "Small");
			params.put("AssociateTag", "a");

			q.setCustomQuery(params);

			return helper.sign(params);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

	}

	public static SortedSet<BookInfo> getInfo(Query q) {
		SortedSet<BookInfo> set = new TreeSet<>();
		try {

			String query = createQuery(q);
			log.info("検索クエリ:{}", query);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(query);
			NodeList titleNode = doc.getElementsByTagName("Title");
			NodeList authorNode = doc.getElementsByTagName("Author");
			NodeList manufacturerNode = doc
					.getElementsByTagName("Manufacturer");

			for (int i = 0; i < titleNode.getLength(); i++) {
				String title = titleNode.item(i).getTextContent();
				String author = authorNode.item(i).getTextContent();
				String pub = manufacturerNode.item(i).getTextContent();
				log.info(title + "  " + author + "  " + pub);

				BookInfo bookInfo = new BookInfo(pub, "", author, title, "");
				set.add(bookInfo);

			}

			if (log.isDebugEnabled()) {
				log.debug(document2String(doc).replaceAll("><", ">\n<"));
			}
		} catch (ParserConfigurationException | SAXException | IOException
				| RuntimeException e) {

			log.error("想定外のエラー", e);

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