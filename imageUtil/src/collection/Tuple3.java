package collection;

import java.util.Comparator;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class Tuple3<V1, V2, V3> implements Comparable<Tuple3<V1, V2, V3>> {

	public Comparator<Tuple3<V1, V2, V3>> comparator1 = new Comparator<Tuple3<V1, V2, V3>>() {

		@Override
		public int compare(Tuple3<V1, V2, V3> o1, Tuple3<V1, V2, V3> o2) {

			return new CompareToBuilder().append(o1.val1, o2.val1)

			.toComparison();
		}

	};
	public Comparator<Tuple3<V1, V2, V3>> comparator2 = new Comparator<Tuple3<V1, V2, V3>>() {

		@Override
		public int compare(Tuple3<V1, V2, V3> o1, Tuple3<V1, V2, V3> o2) {

			return new CompareToBuilder().append(o1.val2, o2.val2)

			.toComparison();
		}

	};
	public Comparator<Tuple3<V1, V2, V3>> comparator3 = new Comparator<Tuple3<V1, V2, V3>>() {

		@Override
		public int compare(Tuple3<V1, V2, V3> o1, Tuple3<V1, V2, V3> o2) {

			return new CompareToBuilder().append(o1.val3, o2.val3)

			.toComparison();
		}

	};

	public static <P1, P2> Tuple3<P1, P2, Void> newT(P1 v1, P2 v2) {
		return new Tuple3<>(v1, v2, (Void) null);

	}

	public static <P1, P2, P3> Tuple3<P1, P2, P3> newT(P1 v1, P2 v2, P3 v3) {
		return new Tuple3<>(v1, v2, v3);

	}

	public Tuple3(V1 val1, V2 val2, V3 val3) {
		super();
		this.val1 = val1;
		this.val2 = val2;
		this.val3 = val3;
	}

	public Tuple3(V1 val1, V2 val2) {
		super();
		this.val1 = val1;
		this.val2 = val2;
	}

	public V1 val1;
	public V2 val2;
	public V3 val3;

	@Override
	public String toString() {
		return "[" + val1 + "][" + val2 + "]" + "][" + val3 + "]";
	}

	@Override
	public int compareTo(Tuple3<V1, V2, V3> o) {

		return new CompareToBuilder().append(this.val1, o.val1)
				.append(this.val2, o.val2).append(this.val3, o.val3)
				.toComparison();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((val1 == null) ? 0 : val1.hashCode());
		result = prime * result + ((val2 == null) ? 0 : val2.hashCode());
		result = prime * result + ((val3 == null) ? 0 : val3.hashCode());
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
		Tuple3<V1, V2, V3> other = (Tuple3<V1, V2, V3>) obj;
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
		if (val3 == null) {
			if (other.val3 != null)
				return false;
		} else if (!val3.equals(other.val3))
			return false;
		return true;
	}

}
