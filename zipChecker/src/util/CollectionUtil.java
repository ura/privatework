package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**

 *
 */
public class CollectionUtil {

	private static Logger log = LoggerFactory.getLogger(CollectionUtil.class);
	//1280x720 DivX651
	private static Pattern viweSize = Pattern.compile("\\d{3,4}x\\d{3,4}");
	private static Pattern divx = Pattern.compile("divx[\\d\\.]{1,7}");

	private static Pattern xvid = Pattern.compile("xvid[\\d\\.]{1,7}");
	private static Pattern wmv = Pattern.compile("wmv\\d{0,4}");

	//雑誌スキャン
	//アニメ映画

	private static Collection<Pattern> videoPattern = new ArrayList<Pattern>();
	static {
		videoPattern.add(viweSize);
		videoPattern.add(divx);
		videoPattern.add(xvid);
		videoPattern.add(wmv);
	}

	public static List<File> conv(Collection<String> array) {
		ArrayList<File> list = new ArrayList<File>();

		for (String a : array) {
			list.add(new File(a));

		}

		return list;

	}

	public static Collection<String> nameVideoFilter(
			Collection<String> collection) {
		nameFilter(collection, videoPattern, true);

		return collection;
	}

	/**
	 *
	 * @param collection
	 * @param patterns
	 * @param matchDeleteFlag trueの場合、一致したものを削除。falseの場合不一致のものを削除
	 * @return
	 */
	public static Collection<String> nameFilter(Collection<String> collection,
			String[] patterns, boolean matchDeleteFlag) {

		Collection<Pattern> patternsReg = new ArrayList<Pattern>();

		for (String pattern : patterns) {

			patternsReg.add(Pattern.compile(pattern));
		}

		return nameFilter(collection, patternsReg, matchDeleteFlag);
	}

	/**
	 *
	 * @param collection
	 * @param patterns
	 * @param matchDeleteFlag trueの場合、一致したものを削除。falseの場合不一致のものを削除
	 * @return
	 */
	public static Collection<String> nameFilter(Collection<String> collection,
			Collection<Pattern> patterns, boolean matchDeleteFlag) {

		Iterator<String> iterator = collection.iterator();
		while (iterator.hasNext()) {
			String s = iterator.next();

			for (Pattern pattern : patterns) {
				boolean b = pattern.matcher(s.toLowerCase()).find();

				if (!(pattern.matcher(s.toLowerCase()).find() ^ matchDeleteFlag)) {
					log.debug("remove {},pattern {}",
							new Object[] { s, pattern.pattern() });
					iterator.remove();
					break;

				}
			}
		}

		return collection;
	}

	public static <E> SortedSet<E> toSortSet(E[] args) {
		TreeSet<E> t = new TreeSet<E>();
		for (E e : args) {
			t.add(e);
		}

		return t;

	}

	public static <E> void addAll(Collection<E> c, E[] args) {

		for (E e : args) {
			c.add(e);
		}

	}

	public static <E> Map<E, Counter> count(Map<E, Counter> map,
			Collection<E> col, int total) {

		if (map == null) {
			map = new HashMap<E, Counter>();
		}
		for (E e : col) {
			if (map.containsKey(e)) {
				map.get(e).increment();
			} else {
				map.put(e, new Counter(1, total));
			}
		}
		return map;

	}

	public static <E> Map<E, Counter> count(Map<E, Counter> map, E[] args,
			int total) {
		if (map == null) {
			map = new HashMap<E, Counter>();
		}
		for (E e : args) {
			if (map.containsKey(e)) {
				map.get(e).increment();
			} else {
				map.put(e, new Counter(1, total));
			}
		}
		return map;

	}

	public static class Counter {

		public int count = 0;
		public int total;;

		public Counter(int count, int total) {

			this.count = count;
			this.total = total;
		}

		public int increment() {
			count++;
			return count;
		}

		public int per() {
			return 100 * count / total;
		}

	}
}
