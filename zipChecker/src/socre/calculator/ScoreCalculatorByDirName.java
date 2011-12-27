package socre.calculator;

import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import socre.name.FileNameParser;
import util.StringUtil;
import util.file.Dir;

import com.google.inject.Inject;
import com.google.inject.name.Named;


/**
 *
 */
public class ScoreCalculatorByDirName implements ScoreCalculator {

	private static Logger log = LoggerFactory
			.getLogger(ScoreCalculatorByDirName.class);

	private FileNameParser fileNameParser;

	@Inject
	public ScoreCalculatorByDirName(
			@Named("normal") FileNameParser fileNameParser) {
		super();
		this.fileNameParser = fileNameParser;
	}

	/**
	 * ロジック：
	 * 「フォルダのパス」と「ファイル名」を比較、検討する。
	 *
	 *
	 * @param dir
	 * @param filePart ファイル名。分割済み。分割してある必要があるのか？？？
	 * @return
	 */
	public int score(Dir dir, String fileName) {

		Collection<String> set = fileNameParser.parse(FilenameUtils
				.getName(fileName));

		int score = 0;
		for (String filePart : set) {
			//ファイル名がフォルダ名（パス）を含んでいたら、、、
			//自身のフォルダ名が直接入っている必要はない。
			// [data][avi][世界遺産]
			//というフォルダ構造なら、

			Collection<String> dirPartSet = new HashSet<String>();

			//各フォルダ名を分解して集積。
			//フォルダ名ごとに同様の名前が含まれていたばあいに対する対応
			// *****アニメ\\****アニメ\\******アニメ
			//みたいな・・・・
			for (String dirPart : dir.nameSet) {
				dirPartSet.addAll(fileNameParser.parse(dirPart));
			}

			for (String dirPart : dirPartSet) {
				if (StringUtil.contain(dirPart, filePart)) {
					score = score + 100;
					break;
				}
			}

		}

		return score;
	}

}
