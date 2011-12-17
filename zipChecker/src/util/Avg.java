package util;

import java.util.SortedSet;
import java.util.TreeSet;

public class Avg {

	private SortedSet<Integer> set = new TreeSet<Integer>();

	public Avg() {

	}

	public void add(int i) {

		set.add(i);

	}

	public int avg() {
		return avg(new NonFilter());
	}

	public int avg(Filter f) {

		double sum = 0;
		int recode = 0;

		int max = set.first().intValue();
		int min = set.last().intValue();
		int num = set.size();

		int index = 0;
		for (Integer i : set) {

			if (f.filter(max, min, num, index)) {

				sum += i.intValue();
				recode++;
			}
			index++;

		}

		return (int) sum / recode;

	}

	static public class Filter {
		public boolean filter(int max, int min, int num, int index) {
			return true;
		}
	}

	static public class NonFilter extends Filter {
		@Override
		public boolean filter(int max, int min, int num, int index) {
			return true;
		}
	}

}
