package socre.calculator;

import dir.Dir;

/**
 * スコアの算出。
 * @author poti
 *
 */
public interface ScoreCalculator {

	/**
	 * @param dir
	 * @param set
	 * @return
	 */
	public int score(Dir dir, String fileName);

}
