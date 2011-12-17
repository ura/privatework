package webapi;

public class BookInfo implements Comparable<BookInfo> {

	public BookInfo(String publisherName, String seriesName, String author,String title) {
		super();
		this.seriesName = seriesName;
		this.publisherName = publisherName;
		this.author = author;
		this.title = title;
	}

	private String seriesName;
	private String publisherName;
	private String author;
	private String title;

	public String getInfo() {

		if (seriesName.equals("")) {

			return "[" + publisherName + "]" + "[" + seriesName + "]" + "["
					+ author + "]";
		} else {
			return "[" + publisherName + "]" + "[" + author + "]";

		}
	}

	@Override
	public String toString() {
		// TODO 自動生成されたメソッド・スタブ
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
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
