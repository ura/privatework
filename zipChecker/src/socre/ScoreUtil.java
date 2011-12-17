package socre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import socre.calculator.ScoreCalculator;
import socre.calculator.ScoreCalculatorByDirFile;
import socre.calculator.ScoreCalculatorByDirName;
import socre.name.FileNameParseImpl;
import util.DebugUtil;
import dir.Dir;

/**
 * 距離判別
 * @author poti
 *
 */
public class ScoreUtil {

	private static Logger log = LoggerFactory.getLogger(ScoreUtil.class);

	public static ScoreUtil createDefault() {
		ScoreUtil n = new ScoreUtil();

		//フォルダに入っているファイルを元に
		n.registSocreCalculator(new ScoreCalculatorByDirFile(
				new FileNameParseImpl()));

		//フォルダのパスを元に
		n.registSocreCalculator(new ScoreCalculatorByDirName(
				new FileNameParseImpl()));

		return n;
	}

	private List<ScoreCalculator> calcList = new ArrayList<ScoreCalculator>();

	public void registSocreCalculator(ScoreCalculator calc) {

		this.calcList.add(calc);
	}

	public Dir dir(Collection<Dir> dirs, String fileName) {
		return dir(dirs, fileName, 100);

	}

	/**
	 * 距離判別を行う。
	 * 一番スコアが高いファルダを選び出す。
	 * @param dirs 候補となるフォルダのセット
	 * @param fileName 判定対象となるファイル。フルパス。
	 * @return
	 */
	public Dir dir(Collection<Dir> dirs, String fileName, int score) {

		SortedSet<ScoreInfo> set = new TreeSet<ScoreInfo>();
		//もっとも、ハイスコアのフォルダを探す。
		for (Dir dir : dirs) {
			ScoreInfo info = calcFileScore(dir, fileName);
			set.add(info);
		}

		if (log.isInfoEnabled()) {
			debug(set);
		}

		if (set.isEmpty()) {
			return null;
		}

		ScoreInfo info = set.first();

		if (info.score >= score) {
			return info.dir;
		} else {
			return null;
		}

	}

	private static void debug(SortedSet<ScoreInfo> set) {
		int i = 0;
		for (ScoreInfo scoreInfo : set) {
			log.info(scoreInfo.toString());
			i++;
			if (i > 2) {
				break;
			}

		}
	}

	/**
	 * あるフォルダのスコアを算出する。
	 * ロジックは指定されるものを複数私用する。
	 * @param dir
	 * @param set
	 * @return
	 */
	private ScoreInfo calcFileScore(Dir dir, String fileName) {
		ScoreInfo info = new ScoreInfo(dir);

		for (ScoreCalculator calc : this.calcList) {
			info.add(calc, calc.score(dir, fileName));
		}
		return info;

	}

	/**
	 *
	 * @author poti
	 *
	 */
	static class ScoreInfo implements Comparable<ScoreInfo> {

		int score;
		Dir dir;
		Map<ScoreCalculator, Integer> map = new HashMap<ScoreCalculator, Integer>();

		public ScoreInfo(Dir dir) {
			super();
			this.dir = dir;
		}

		public void add(ScoreCalculator s, int score) {
			this.score = this.score + score;
			map.put(s, score);
		}

		/**
		 * 降順
		 */
		@Override
		public int compareTo(ScoreInfo o) {

			return o.score - this.score;
		}

		@Override
		public String toString() {

			StringBuilder sb = new StringBuilder();
			for (Map.Entry<ScoreCalculator, Integer> e : map.entrySet()) {
				sb.append(DebugUtil.getClassName(e.getKey())).append(" = ")
						.append(e.getValue()).append(",");
			}

			return "ScoreInfo [dir=" + dir + ", ScoreCalculator=" + sb
					+ " score=" + score + "]";
		}

	}

}
