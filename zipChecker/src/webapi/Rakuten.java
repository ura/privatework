package webapi;

import java.io.CharArrayReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
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

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.web2driver.abaron.client.AbaronRESTClient;
import com.web2driver.abaron.client.AbaronResultNode;

public class Rakuten {

	private static final String KEY = "572475de8b4c52837e32a6777584c734";

	public static void main(String[] args) throws Exception {

		test("Psyren");
		test("龍狼伝");
		test("龍狼伝　(２");

	}

	private static void test(String key) {

		Set<String> author = getAuthor(key);

		for (String string : author) {
			System.out.println(key + "\t" + string);
		}

		SortedSet<BookInfo> info = getInfo(key);

		for (BookInfo i : info) {
			System.out.println(key + "\t" + i.getInfo());
		}



	}

	private static String createName(String key) {

		SortedSet<BookInfo> info = getInfo(key);
		for (BookInfo bookInfo : info) {

			return "["+bookInfo.getPublisherName()+"][" + bookInfo.getSeriesName()
					+"][" + bookInfo.getAuthor()+"]["+bookInfo.getAuthor()+"]";
		}
		return null;

	}

	private static AbaronResultNode getWebInfo(String title) {
		AbaronRESTClient stub = new AbaronRESTClient();

		// Amazon WebサービスのエンドポイントURL
		stub.setEndpointUrl("http://api.rakuten.co.jp/rws/3.0/rest");

		// URLパラメータを設定する
		stub.setParameter("developerId", KEY);
		stub.setParameter("operation", "BooksBookSearch");
		stub.setParameter("version", "2010-03-18");
		stub.setParameter("title", title);
		stub.setParameter("Operation", "ItemLookup");
		stub.setParameter("ResponseGroup", "Small");
		stub.setParameter("booksGenreID", "001");
		stub.setParameter("size", "9");

		// Webサービスを呼び出す
		AbaronResultNode result = stub.doRequest();
		return result;
	}

	private static Document createDoc(String xmlString) throws SAXException,
			IOException, ParserConfigurationException {

		xmlString = xmlString.substring(xmlString.indexOf("<Body>"), xmlString
				.indexOf("</Body>")
				+ "</Body>".length());

		xmlString = xmlString.replace(":BooksBookSearch", "");

		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new CharArrayReader(
				xmlString.toCharArray())));

		return doc;
	}

	public static SortedSet<BookInfo> getInfo(String title) {

		SortedSet<BookInfo> set = new TreeSet<BookInfo>();
		AbaronResultNode result = getWebInfo(title);

		result.print(false);

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


					set.add(new BookInfo(publisherName, seriesName, author,t));

				}
			}

		} catch (XPathExpressionException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			result.print(false);
		} catch (DOMException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			result.print(false);
		} catch (ParserConfigurationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			result.print(false);
		} catch (SAXException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			result.print(false);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			result.print(false);
		} catch (RuntimeException e) {
			e.printStackTrace();
			result.print(false);
		}
		return set;
	}

	public static SortedSet<String> getAuthor(String title) {

		SortedSet<String> set = new TreeSet<String>();
		AbaronResultNode result = getWebInfo(title);

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

		} catch (XPathExpressionException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			result.print(false);
		} catch (DOMException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			result.print(false);
		} catch (ParserConfigurationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			result.print(false);
		} catch (SAXException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			result.print(false);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			result.print(false);
		} catch (RuntimeException e) {
			e.printStackTrace();
			result.print(false);
		}
		return set;
	}
}
