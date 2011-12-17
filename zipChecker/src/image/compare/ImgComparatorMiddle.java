package image.compare;

import image.Histogram;
import image.Img;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImgComparatorMiddle extends SimpleHistogramComparator {

	private static Logger log = LoggerFactory
			.getLogger(ImgComparatorMiddle.class);

	private int level1Limit;
	private int level2Limit;

	public ImgComparatorMiddle(int level1Limit, int level2Limit) {
		super();
		this.level1Limit = level1Limit;
		this.level2Limit = level2Limit;
	}

	@Override
	public int comparate(Img image1, Img image2) {

		if (image1.equalImage(image2)) {
			return 0;

		} else {

			int result = comparate(image1.getHistogram(), image2.getHistogram());

			if (result < level1Limit) {
				return result;
			} else if (result < level2Limit) {
				int result2 = comparate(image1
						.createHistogram(Histogram.Settings.MID), image2
						.createHistogram(Histogram.Settings.MID));

				log.info("result1:{}\tresult2:{}", result, result2);
				return result2;

			} else {
				return result;
			}
		}

	}
}
