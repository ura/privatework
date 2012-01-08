package book.webapi;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Normalizer;

public class BookInfo implements Comparable<BookInfo>, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 311214406048533356L;

	private static Logger log = LoggerFactory.getLogger(BookInfo.class);

	private static final Pattern titleSReg = Pattern
			.compile("(.*)[\\s　]+([0-9]+)$");

	private static final Pattern titleReg0 = Pattern
			.compile("([^0-9０-９(（]*)[ 　][(（]第([0-9０-９]+)巻.*");

	private static final Pattern titleReg1 = Pattern
			.compile("(.*)[\\(（]{1}([    ([0-9]]+)[）\\)]{1}$");

	private static final Pattern titleReg2 = Pattern
			.compile("(.*[^0-9(（])[(（]*([0-9]{1,2})([^0-9]+.*) [\\(\\(（].*[\\)）\\)]$");
	private static final Pattern titleReg7 = Pattern
			.compile("([^()（）]*)[()（）]([0-9０-９]+)[()（）]");

	private static final Pattern titleReg3 = Pattern
			.compile("(.*[^0-9()])([0-9０-９]+)(.*[^0-9]) [\\(\\(（].*[\\)）\\)]$");
	private static final Pattern titleReg4 = Pattern
			.compile("(.*[^0-9])[(（]第([0-9]*)巻[）)]$");
	private static final Pattern titleReg5 = Pattern
			.compile("(.*)[\\(（]{1}([    ([0-9]]+)[）\\)]{1}");
	private static final Pattern titleReg8 = Pattern
			.compile("([^0-9０-９(（]*)[ 　]([0-9０-９]+) [(（].*");
	private static final Pattern titleReg6 = Pattern
			.compile("(.*[^0-9(（])[(（ ]*([0-9]+)[^0-9]*$");

	/**
	 * 巻数取得あり
	 */
	private static final Pattern bookInfoReg1 = Pattern
			.compile("\\[(.*)\\]\\[(.*)\\]\\[(.*) 第([0-9]+)巻\\]\\[ISBN([0-9A-Za-z ]*)\\]");
	/**
	 * 巻数なし
	 */
	private static final Pattern bookInfoReg2 = Pattern
			.compile("\\[(.*)\\]\\[(.*)\\]\\[(.*)\\]\\[ISBN([0-9A-Za-z ]*)\\]");

	private static final List<Pattern> regList;
	static {
		ArrayList<Pattern> list = new ArrayList<Pattern>();
		list.add(titleReg0);
		list.add(titleSReg);
		list.add(titleReg1);
		list.add(titleReg2);
		list.add(titleReg7);

		list.add(titleReg8);
		list.add(titleReg3);
		list.add(titleReg4);
		list.add(titleReg5);
		list.add(titleReg6);

		regList = Collections.unmodifiableList(list);

	}

	public static boolean isBookInfoName(String name) {

		Matcher matcher = bookInfoReg1.matcher(name);
		Matcher matcher2 = bookInfoReg2.matcher(name);
		return matcher.find() || matcher2.find();

	}

	public static BookInfo createBookInfo(String name) {
		//[別天荒人][集英社][明日泥棒 第04巻][ISBN9784088776453][抜けあり]

		BookInfo bookInfo = new BookInfo();

		Matcher matcher = bookInfoReg1.matcher(name);
		if (matcher.find()) {
			bookInfo.setAuthor(matcher.group(1));
			bookInfo.setPublisherName(matcher.group(2));
			bookInfo.setTitleStr(matcher.group(3));
			bookInfo.setNo(matcher.group(4));
			bookInfo.setIsbn(matcher.group(5));
			return bookInfo;
		}
		Matcher matcher2 = bookInfoReg2.matcher(name);
		if (matcher2.find()) {
			bookInfo.setAuthor(matcher2.group(1));
			bookInfo.setPublisherName(matcher2.group(2));
			bookInfo.setTitleStr(matcher2.group(3));

			bookInfo.setIsbn(matcher2.group(4));
			return bookInfo;
		}

		throw new IllegalStateException("ロジックがバグっている");

	}

	public static boolean isBookInfoName(File dir) {

		return isBookInfoName(dir.getName());

	}

	public static BookInfo createBookInfo(File dir) {
		return createBookInfo(dir.getName());
	}

	public BookInfo() {
		this.no = "";
		this.seriesName = "";
	}

	public BookInfo(String title) {
		super();
		this.seriesName = "";
		this.publisherName = "";
		this.author = "";
		this.rowTitle = title;
		this.isbn = "";
		this.no = "";
		this.titleStr = "";
		this.rowdateOnly = true;

	}

	public BookInfo(String publisherName, String seriesName, String author,
			String title, String isbn) {
		super();
		this.seriesName = Normalizer.normalizer(seriesName);
		this.publisherName = Normalizer.normalizer(publisherName);
		this.author = Normalizer.normalizer(author);
		this.rowTitle = title;
		this.isbn = isbn;

		init();
	}

	private void init() {

		this.titleStr = this.rowTitle;
		this.no = "";
		log.info("base:{}", rowTitle);

		for (Pattern reg : regList) {
			Matcher matcher = reg.matcher(this.rowTitle);

			boolean result = matcher.find();

			if (result) {

				this.titleStr = Normalizer.normalizer(matcher.group(1));
				this.no = no_XX(matcher.group(2));

				log.info("base:{}  title:{}  NO:{}  REG:{}  ISBN:{}",
						new String[] { this.rowTitle, this.titleStr, this.no,
								reg.pattern(), this.isbn });

				break;

			}

		}
		if (no.equals("")) {
			log.warn("パース用正規表現が見つかりませんでした。{}", rowTitle);
		}

	}

	private static String no_XX(String xx) {

		return no_XX(Integer.parseInt(xx));

	}

	private static String no_XX(int x) {
		DecimalFormat nf = new DecimalFormat("##");
		nf.setMinimumIntegerDigits(2);
		return nf.format(x);

	}

	private String seriesName;
	private String publisherName;
	private String author;
	private String rowTitle;
	private String isbn;
	private boolean rowdateOnly = false;

	/**
	 * タイトル部分と思わしき部分を切り出します。
	 */
	private String titleStr;
	/**
	 * 巻数を切り出します。ない場合は空文字です。
	 */
	private String no;

	public String getInfo() {
		if (rowdateOnly) {
			return rowTitle;
		}

		if (!seriesName.equals("")) {

			return "[" + author + "]" + "[" + publisherName + "]" + "["
					+ titleStr + (haveNo() ? " 第" + no + "巻" : "") + "]"
					+ "[ISBN" + isbn + "]";
		} else {
			return "[" + author + "]" + "[" + publisherName + "]" + "["
					+ titleStr + (haveNo() ? " 第" + no + "巻" : "") + "]"
					+ "[ISBN" + isbn + "]";

		}
	}

	public boolean isTrueISBN() {
		return this.isbn.startsWith("978");
	}

	public String getBaseInfo() {
		if (rowdateOnly) {
			return rowTitle;
		}

		if (!seriesName.equals("")) {

			return "[" + author + "]" + "[" + publisherName + "]" + "["
					+ titleStr + "]";
		} else {
			return "[" + author + "]" + "[" + publisherName + "]" + "["
					+ titleStr + "]";

		}
	}

	public boolean haveNo() {
		return !no.equals("");
	}

	@Override
	public String toString() {

		return getInfo();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((isbn == null) ? 0 : isbn.hashCode());
		result = prime * result + ((no == null) ? 0 : no.hashCode());
		result = prime * result
				+ ((publisherName == null) ? 0 : publisherName.hashCode());
		result = prime * result
				+ ((rowTitle == null) ? 0 : rowTitle.hashCode());
		result = prime * result + (rowdateOnly ? 1231 : 1237);
		result = prime * result
				+ ((seriesName == null) ? 0 : seriesName.hashCode());
		result = prime * result
				+ ((titleStr == null) ? 0 : titleStr.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BookInfo other = (BookInfo) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		if (isbn == null) {
			if (other.isbn != null)
				return false;
		} else if (!isbn.equals(other.isbn))
			return false;
		if (no == null) {
			if (other.no != null)
				return false;
		} else if (!no.equals(other.no))
			return false;
		if (publisherName == null) {
			if (other.publisherName != null)
				return false;
		} else if (!publisherName.equals(other.publisherName))
			return false;
		if (rowTitle == null) {
			if (other.rowTitle != null)
				return false;
		} else if (!rowTitle.equals(other.rowTitle))
			return false;
		if (rowdateOnly != other.rowdateOnly)
			return false;
		if (seriesName == null) {
			if (other.seriesName != null)
				return false;
		} else if (!seriesName.equals(other.seriesName))
			return false;
		if (titleStr == null) {
			if (other.titleStr != null)
				return false;
		} else if (!titleStr.equals(other.titleStr))
			return false;
		return true;
	}

	@Override
	public int compareTo(BookInfo o) {

		return getInfo().compareTo(o.getInfo());

	}

	public String getSeriesName() {
		return seriesName;
	}

	public void setSeriesName(String seriesName) {
		this.seriesName = Normalizer.normalizer(seriesName);
	}

	public String getPublisherName() {
		return publisherName;
	}

	public void setPublisherName(String publisherName) {
		this.publisherName = Normalizer.normalizer(publisherName);
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = Normalizer.normalizer(author);
	}

	public String getNo() {
		return no;
	}

	public boolean isRowdateOnly() {
		return rowdateOnly;
	}

	public String getRowTitle() {
		return rowTitle;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getTitleStr() {
		return titleStr;
	}

	public void setTitleStr(String titleStr) {
		this.titleStr = Normalizer.normalizer(titleStr);
	}

	public void setRowTitle(String rowTitle) {
		this.rowTitle = rowTitle;
	}

	public void setRowdateOnly(boolean rowdateOnly) {
		this.rowdateOnly = rowdateOnly;
	}

	public void setNo(String no) {
		this.no = no;
	}

}
