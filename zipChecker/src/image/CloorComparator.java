package image;

import image.Histogram.CloorBlock;

import java.util.Comparator;

public class CloorComparator implements Comparator<CloorBlock> {

	@Override
	public int compare(CloorBlock o1, CloorBlock o2) {

		return -(int)(o1.count-o2.count);
	}




}
