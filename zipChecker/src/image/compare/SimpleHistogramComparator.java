package image.compare;

import image.Histogram;
import image.Img;
import image.Histogram.CloorBlock;
import image.Histogram.HistogramBlock;

import java.util.Set;
import java.util.Map.Entry;


public class SimpleHistogramComparator implements ImgComparator {

	@Override
	public int comparate(Img image1, Img image2) {

		if (image1.equalImage(image2)) {
			return 0;
		} else {

			return comparate(image1.getHistogram(), image2.getHistogram());
		}
	}

	public int comparate(Histogram h1, Histogram h2) {

		HistogramBlock[][] a1 = h1.getHistogramBlockArray();
		HistogramBlock[][] a2 = h2.getHistogramBlockArray();

		if (a1.length == a2.length) {
			if (a1[0].length == a2[0].length) {

				return (int) Math.sqrt(comparate(a1, a2));

			}
		}

		throw new IllegalArgumentException("分割度が揃っていません");

	}

	public int comparate(HistogramBlock[][] b1, HistogramBlock[][] b2) {

		// TODO 単純に平均とるか、要検討
		Avg avg = new Avg();

		for (int i = 0; i < b1.length; i++) {
			HistogramBlock[] histogramBlocks = b1[i];
			for (int j = 0; j < histogramBlocks.length; j++) {

				avg.add(comparate(b1[i][j], b2[i][j]));
			}

		}

		return avg.avg();

	}

	public int comparate(HistogramBlock b1, HistogramBlock b2) {
		Avg avg = new Avg();

		Set<Entry<String, CloorBlock>> entrySet1 = b1.getSet().entrySet();
		Set<Entry<String, CloorBlock>> entrySet2 = b1.getSet().entrySet();

		for (Entry<String, CloorBlock> entry : entrySet1) {

			String key = entry.getKey();

			int v1 = entry.getValue().rate();
			CloorBlock cloorBlock = b2.getSet().get(key);

			int v2;

			if (cloorBlock != null) {
				v2 = cloorBlock.rate();
			} else {
				v2 = 0;
			}

			avg.add(Math.abs(v1 - v2) * Math.abs(v1 - v2));

		}
		for (Entry<String, CloorBlock> entry : entrySet2) {

			String key = entry.getKey();

			int v1 = entry.getValue().rate();
			CloorBlock cloorBlock = b1.getSet().get(key);

			int v2;

			if (cloorBlock != null) {
				v2 = cloorBlock.rate();
			} else {
				v2 = 0;
			}

			avg.add(Math.abs(v1 - v2) * Math.abs(v1 - v2));

		}

		return avg.avg();
	}

	public int comparate(int[] b1, int[] b2) {

		Avg avg = new Avg();
		for (int i = 0; i < b1.length; i++) {
			int cloor1 = b1[i];
			int cloor2 = b2[i];

			avg.add(Math.abs(cloor1 - cloor2));

		}

		return avg.avg();

	}

}
