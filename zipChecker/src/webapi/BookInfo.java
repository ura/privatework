package webapi;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookInfo implements Comparable<BookInfo> {

	private static Logger log = LoggerFactory.getLogger(BookInfo.class);

	private static final Pattern titleSReg = Pattern
			.compile("(.*)[\\s　]+([0-9]+)$");

	private static final Pattern titleReg1 = Pattern
			.compile("(.*)[(\\（]([    ([0-9]+])[）\\)]$");

	private static final List<Pattern> regList;
	static {
		ArrayList<Pattern> list = new ArrayList<Pattern>();
		list.add(titleSReg);
		list.add(titleReg1);

		regList = Collections.unmodifiableList(list);

	}

	public BookInfo(String publisherName, String seriesName, String author,
			String title) {
		super();
		this.seriesName = seriesName;
		this.publisherName = publisherName;
		this.author = author;
		this.rowTitle = title;

		init();
	}

	private void init() {

		this.titleStr = this.rowTitle;
		this.no = "";

		for (Pattern reg : regList) {
			Matcher matcher = reg.matcher(this.rowTitle);

			boolean result = matcher.find();
			log.info(no + "\t" + reg.pattern() + "\t" + result);

			if (result) {
				this.titleStr = matcher.group(1);
				this.no = no_XX(matcher.group(2));

				break;

			}

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

	/**
	 * タイトル部分と思わしき部分を切り出します。
	 */
	private String titleStr;
	/**
	 * 巻数を切り出します。ない場合は空文字です。
	 */
	private String no;

	public String getInfo() {

		if (seriesName.equals("")) {

			return "[" + author + "]" + "[" + publisherName + "]" + "["
					+ seriesName + "]" + "[" + titleStr
					+ (haveNo() ? " 第" + no + "巻" : "") + "]";
		} else {
			return "[" + author + "]" + "[" + publisherName + "]" + "[" + "["
					+ titleStr + (haveNo() ? " 第" + no + "巻" : "") + "]";

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
		result = prime * result
				+ ((publisherName == null) ? 0 : publisherName.hashCode());
		result = prime * result
				+ ((seriesName == null) ? 0 : seriesName.hashCode());
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
		if (publisherName == null) {
			if (other.publisherName != null)
				return false;
		} else if (!publisherName.equals(other.publisherName))
			return false;
		if (seriesName == null) {
			if (other.seriesName != null)
				return false;
		} else if (!seriesName.equals(other.seriesName))
			return false;
		return true;
	}

	@Override
	public int compareTo(BookInfo o) {

		return this.publisherName.compareTo(o.publisherName);
	}

	public String getSeriesName() {
		return seriesName;
	}

	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	public String getPublisherName() {
		return publisherName;
	}

	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getTitle() {
		return rowTitle;
	}

	public void setTitle(String title) {
		this.rowTitle = title;
	}

}
