package collection;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class Tuple<V1, V2, V3> implements Comparable<Tuple> {

	public static <P1, P2> Tuple<P1, P2, Void> newT(P1 v1, P2 v2) {
		return new Tuple<>(v1, v2, (Void) null);

	}

	public static <P1, P2, P3> Tuple<P1, P2, P3> newT(P1 v1, P2 v2, P3 v3) {
		return new Tuple<>(v1, v2, v3);

	}

	public Tuple(V1 val1, V2 val2, V3 val3) {
		super();
		this.val1 = val1;
		this.val2 = val2;
		this.val3 = val3;
	}

	public Tuple(V1 val1, V2 val2) {
		super();
		this.val1 = val1;
		this.val2 = val2;
	}

	public V1 val1;
	public V2 val2;
	public V3 val3;

	@Override
	public int compareTo(Tuple o) {

		return new CompareToBuilder().append(this.val1, o.val1)
				.append(this.val2, o.val2).append(this.val3, o.val3)
				.toComparison();
	}

}
