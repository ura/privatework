package collection;

/**
 * @deprecated GUAVA使え
 * @author name
 *
 */
public class SortKey<K1 extends Comparable, K2> implements
		Comparable<SortKey<K1, K2>> {

	private K1 key1;
	private K2 key2;

	public SortKey(K1 k1, K2 k2) {
		this.key1 = k1;
		this.key2 = k2;

	}

	@Override
	public int compareTo(SortKey<K1, K2> o) {

		return this.key1.compareTo(o.key1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key1 == null) ? 0 : key1.hashCode());
		result = prime * result + ((key2 == null) ? 0 : key2.hashCode());
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
		SortKey other = (SortKey) obj;
		if (key1 == null) {
			if (other.key1 != null)
				return false;
		} else if (!key1.equals(other.key1))
			return false;
		if (key2 == null) {
			if (other.key2 != null)
				return false;
		} else if (!key2.equals(other.key2))
			return false;
		return true;
	}

	public K1 getKey1() {
		return key1;
	}

	public K2 getKey2() {
		return key2;
	}

}
