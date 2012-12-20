package image.compare;

import image.Img;

import java.util.Collection;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompositComparator implements ImgComparator {

	private static Logger log = LoggerFactory
			.getLogger(ImgComparatorMiddle.class);

	private ImgComparator comp;

	private TreeMap<Integer, Img> sameList = new TreeMap<Integer, Img>();
	private TreeMap<Integer, Img> nearList = new TreeMap<Integer, Img>();
	private TreeMap<Integer, Img> otherList = new TreeMap<Integer, Img>();

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
			sameList.put(result, image2);
		} else if (result < nearLimit) {
			nearList.put(result, image2);
		} else {

			otherList.put(result, image2);
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

	private void log(String msg, Img base, TreeMap<Integer, Img> map) {
		for (Entry<Integer, Img> entry : map.entrySet()) {
			int result = entry.getKey();
			Img img2 = entry.getValue();

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
