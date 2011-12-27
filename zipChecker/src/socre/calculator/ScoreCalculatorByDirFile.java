package socre.calculator;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import socre.name.FileNameParser;
import util.CollectionUtil;
import util.StringUtil;
import util.file.Dir;

import com.google.inject.Inject;
import com.google.inject.name.Named;


/**
 * そのフォルダに入っているファイルの特徴をみて、スコアを登録する。
 * 例：あるフォルダに「世界遺産」「.avi」といったファイルばかり入っている場合、
 * その特徴に一致するファイルを高く評価する。
 *

 *
 */
public class ScoreCalculatorByDirFile implements ScoreCalculator {

	private static Logger log = LoggerFactory
			.getLogger(ScoreCalculatorByDirFile.class);

	private FileNameParser fileNameParser;

	@Inject
	public ScoreCalculatorByDirFile(
			@Named("normal") FileNameParser fileNameParser) {
		super();
		this.fileNameParser = fileNameParser;
	}

	/**
	 * ロジック：
	 * フォルダに含まれているファイルの名前を分解する。
	 *
	 *
	 * @param dir
	 * @param filePart ファイル名。分割済み。分割してある必要があるのか？？？
	 * @return
	 */
	public int score(Dir dir, String fileName) {

		Collection<String> set = fileNameParser.parse(FilenameUtils
				.getName(fileName));

		Map<String, CollectionUtil.Counter> map = null;
		int fCount = dir.fileNameSet.size();

		//フォルダの状態を分析
		for (String dirFile : dir.fileNameSet) {
			map = CollectionUtil.count(map,
					this.fileNameParser.parse(FilenameUtils.getName(fileName)),
					fCount);
		}

		int score = 0;
		if (map != null) {
			log.info("FILE FULL.{} ,Path.{} ",
					new Object[] { set, dir.dir.getPath() });

			for (Map.Entry<String, CollectionUtil.Counter> e : map.entrySet()) {
				//フォルダの分析結果をつかって、
				score = score + folder(e, set);
			}
		} else {
			log.debug("Map null:{}", dir.dir.getPath());
		}

		return score;
	}

	/**
	 * フォルダ内の状態の1要素とフォルダ名の要素をスコア化。
	 * @param e
	 * @param set
	 * @return
	 */
	private int folder(Map.Entry<String, CollectionUtil.Counter> e,
			Collection<String> set) {
		for (String filePart : set) {
			if (StringUtil.contain(e.getKey(), filePart)) {
				//フォルダ内で「？」%の勢力の要素があったら、プラス。
				//それ以下は、ノイズとして足きり。
				if (e.getValue().per() > 30) {
					int score = e.getValue().per();
					log.info("FILE.{} ,DIR.{},Per.{},Count.{}",
							new Object[] { filePart, e.getKey(),
									e.getValue().per(), e.getValue().count });

					return score;
				}
			}
		}

		//逆に、そのフォルダの特徴的な要素であるにもかかわらず、
		//みつからなかったら、マイナス。
		//番組名とうが該当
		int per = e.getValue().per();
		if (per > 70) {
			log.info("OUT!!! DIR.{},Per.{},Count.{}", new Object[] {
					e.getKey(), e.getValue().per(), e.getValue().count });
			//70*70 =4900 /40 = 100
			//80*80 =6400 /40 = 160
			//90*90 =8100 /40 = 200
			return -(per * per) / 40;
		} else {
			return 0;
		}

	}
}
