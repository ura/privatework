package socre.calculator;

import util.file.Dir;

/**
 * スコアの算出。

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
