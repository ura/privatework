package collection;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;

/**
 * @deprecated GUAVA使え
 * @author name
 *
 */
public class $C {

	public static abstract class Filter<V> {

		public abstract boolean $(V v);

	}

	public static abstract class Foreach<V> {

		public abstract void $(V v);

	}

	public static abstract class FilterM<K, V> {

		public abstract boolean $(Map.Entry<K, V> v);

	}

	public static abstract class ForeachM<K, V> {

		public abstract void $(Map.Entry<K, V> v);

	}

	public static <V> List<V> filter(List<V> l, Filter<V> f) {

		try {
			List<V> list = l.getClass().newInstance();
			for (V v : l) {

				if (f.$(v)) {
					System.out.println(v);
					list.add(v);
				}
			}
			return list;
		} catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();

		}
		throw new IllegalArgumentException();

	}

	public static <V> void foreach(List<V> l, Foreach<V> f) {

		for (V v : l) {
			f.$(v);
		}
	}

	public static <V> _L<V> filter$(List<V> l, Filter<V> f) {
		List<V> list = filter(l, f);
		return new _L<V>(list);

	}

	public static <V> _L<V> foreach$(List<V> l, Foreach<V> f) {
		for (V v : l) {
			f.$(v);
		}
		return new _L<V>(l);

	}

	/**
	 * SET関連
	 */
	public static <V> Set<V> filter(Set<V> l, Filter<V> f) {

		try {
			Set<V> list = l.getClass().newInstance();
			for (V v : l) {

				if (f.$(v)) {

					list.add(v);
				}
			}
			return list;
		} catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();

		}
		throw new IllegalArgumentException();

	}

	public static <V> void foreach(Set<V> l, Foreach<V> f) {

		for (V v : l) {
			f.$(v);
		}
	}

	public static <V> _S<V> filter$(Set<V> l, Filter<V> f) {
		Set<V> list = filter(l, f);
		return new _S<V>(list);

	}

	public static <V> _S<V> foreach$(Set<V> l, Foreach<V> f) {
		for (V v : l) {
			f.$(v);
		}
		return new _S<V>(l);

	}

	static class _L<V> {

		public final List<V> l;

		public _L(List<V> l) {
			super();
			this.l = l;
		}

		public _L<V> filter(Filter<V> f) {

			_L<V> result = $C.filter$(this.l, f);
			return result;
		}

		public _L<V> foreach(Foreach<V> f) {

			$C.foreach$(this.l, f);
			return this;
		}

	}

	static class _S<V> {

		public final Set<V> l;

		public _S(Set<V> l) {
			super();
			this.l = l;
		}

		public _S<V> filter(Filter<V> f) {

			_S<V> result = $C.filter$(this.l, f);
			return result;
		}

		public _S<V> foreach(Foreach<V> f) {

			$C.foreach$(this.l, f);
			return this;
		}

	}

	/**
	 * SortedSet関連。
	 * <BR>コンパレーターは引き継ぎません</BR>
	 */
	public static <V> SortedSet<V> filter(SortedSet<V> l, Filter<V> f) {

		try {
			SortedSet<V> list = l.getClass().newInstance();

			for (V v : l) {

				if (f.$(v)) {

					list.add(v);
				}
			}
			return list;
		} catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();

		}
		throw new IllegalArgumentException();

	}

	public static <V> void foreach(SortedSet<V> l, Foreach<V> f) {

		for (V v : l) {
			f.$(v);
		}
	}

	public static <V> _Sort<V> filter$(SortedSet<V> l, Filter<V> f) {
		SortedSet<V> list = filter(l, f);
		return new _Sort<V>(list);

	}

	public static <V> _Sort<V> foreach$(SortedSet<V> l, Foreach<V> f) {
		for (V v : l) {
			f.$(v);
		}
		return new _Sort<V>(l);

	}

	static class _Sort<V> {

		public final SortedSet<V> l;

		public _Sort(SortedSet<V> l) {
			super();
			this.l = l;
		}

		public _Sort<V> filter(Filter<V> f) {

			_Sort<V> result = $C.filter$(this.l, f);
			return result;
		}

		public _Sort<V> foreach(Foreach<V> f) {

			$C.foreach$(this.l, f);
			return this;
		}

	}

	/**
	 * Map関連。
	 */
	public static <K, V> Map<K, V> filter(Map<K, V> l, FilterM<K, V> f) {

		try {
			Map<K, V> list = l.getClass().newInstance();

			for (Entry<K, V> v : list.entrySet()) {

				if (f.$(v)) {

					list.put(v.getKey(), v.getValue());
				}
			}
			return list;
		} catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();

		}
		throw new IllegalArgumentException();

	}

	public static <K, V> void foreach(Map<K, V> l, ForeachM<K, V> f) {

		for (Entry<K, V> v : l.entrySet()) {
			f.$(v);
		}
	}

	public static <K, V> _M<K, V> filter$(Map<K, V> l, FilterM<K, V> f) {
		Map<K, V> list = filter(l, f);
		return new _M<K, V>(list);

	}

	public static <K, V> _M<K, V> foreach$(Map<K, V> l, ForeachM<K, V> f) {
		for (Entry<K, V> v : l.entrySet()) {
			f.$(v);
		}
		return new _M<K, V>(l);

	}

	static class _M<K, V> {

		public final Map<K, V> row;

		public _M(Map<K, V> l) {
			super();
			this.row = l;
		}

		public _M<K, V> filter(FilterM<K, V> f) {

			_M<K, V> result = $C.filter$(this.row, f);
			return result;
		}

		public _M<K, V> foreach(ForeachM<K, V> f) {

			$C.foreach$(this.row, f);
			return this;
		}

	}

}
