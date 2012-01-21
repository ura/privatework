package collection;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class Tuple<V1, V2> implements Comparable<Tuple<V1, V2>> {

	public Comparator<Tuple<V1, V2>> comparator1 = new Comparator<Tuple<V1, V2>>() {

		@Override
		public int compare(Tuple<V1, V2> o1, Tuple<V1, V2> o2) {

			return new CompareToBuilder().append(o1.val1, o2.val1)

			.toComparison();
		}

	};
	public Comparator<Tuple<V1, V2>> comparator2 = new Comparator<Tuple<V1, V2>>() {

		@Override
		public int compare(Tuple<V1, V2> o1, Tuple<V1, V2> o2) {

			return new CompareToBuilder().append(o1.val2, o2.val2)

			.toComparison();
		}

	};

	public static <P1, P2> Tuple<P1, P2> newT(P1 v1, P2 v2) {
		return new Tuple<>(v1, v2);

	}

	public Tuple(V1 val1, V2 val2) {
		super();
		this.val1 = val1;
		this.val2 = val2;

	}

	public V1 val1;
	public V2 val2;

	@Override
	public String toString() {
		return "[" + val1 + "][" + val2 + "]" + "]";
	}

	@Override
	public int compareTo(Tuple o) {

		return new CompareToBuilder().append(this.val1, o.val1)
				.append(this.val2, o.val2).toComparison();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((val1 == null) ? 0 : val1.hashCode());
		result = prime * result + ((val2 == null) ? 0 : val2.hashCode());
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
		Tuple other = (Tuple) obj;
		if (val1 == null) {
			if (other.val1 != null)
				return false;
		} else if (!val1.equals(other.val1))
			return false;
		if (val2 == null) {
			if (other.val2 != null)
				return false;
		} else if (!val2.equals(other.val2))
			return false;
		return true;
	}
}
