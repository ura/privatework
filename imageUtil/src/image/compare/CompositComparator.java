package image.compare;

import image.Img;

import java.util.Collection;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import collection.SortKey;

public class CompositComparator implements ImgComparator {

	private static Logger log = LoggerFactory
			.getLogger(ImgComparatorMiddle.class);

	private ImgComparator comp;

	private TreeMap<SortKey<Integer, Img>, Img> sameList = new TreeMap<SortKey<Integer, Img>, Img>();
	private TreeMap<SortKey<Integer, Img>, Img> nearList = new TreeMap<SortKey<Integer, Img>, Img>();
	private TreeMap<SortKey<Integer, Img>, Img> otherList = new TreeMap<SortKey<Integer, Img>, Img>();

	private int sameLimit;
	private int nearLimit = 10000;

	public CompositComparator(ImgComparator comp, int sameLimit) {
		super();
		this.comp = comp;
		this.sameLimit = sameLimit;
	}

	public CompositComparator(ImgComparator comp, int sameLimit, int nearLimit) {
		super();
		this.comp = comp;
		this.sameLimit = sameLimit;
		this.nearLimit = nearLimit;
	}

	public int comparate(Img image1, Img image2) {
		int result = comp.comparate(image1, image2);
		add(image2, result);

		return result;
	}

	public void add(Img image2, int result) {

		if (result < sameLimit) {
			sameList.put(new SortKey<Integer, Img>(result, image2), image2);
		} else if (result < nearLimit) {
			nearList.put(new SortKey<Integer, Img>(result, image2), image2);
		} else {

			otherList.put(new SortKey<Integer, Img>(result, image2), image2);
		}

	}

	public void printInfo(Img base) {
		if (log.isInfoEnabled()) {

			log.info("ベース画像情報:\t\t\t{}\t{}", base.getImgFile().getName(),
					base.getInfo());

			log("sameList  \t" + sameLimit, base, sameList);
			log("nearList  \t" + nearLimit, base, nearList);
			log("otherList \t" + 10000, base, otherList);
			log.info("");
		}
	}

	private void log(String msg, Img base,
			TreeMap<SortKey<Integer, Img>, Img> map) {
		for (Entry<SortKey<Integer, Img>, Img> entry : map.entrySet()) {
			int result = entry.getKey().getKey1();
			Img img2 = entry.getKey().getKey2();

			log.info("{}\t一致度[{}]\t{}\t{} ", new Object[] { msg, result,
					base.getImgFile().getName(), img2.getInfo() });
		}
	}

	public Collection<Img> getSameList() {
		return sameList.values();
	}

	public Collection<Img> getNearList() {
		return nearList.values();
	}

	public Collection<Img> getOtherList() {
		return otherList.values();
	}

}
