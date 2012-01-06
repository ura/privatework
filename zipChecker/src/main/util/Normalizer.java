package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Normalizer {
	private static Map<String, String> hmCharTbl = new HashMap<String, String>();
	static {
		{
			Map<String, String> m = new HashMap<String, String>();
			m.put("0", "０");
			m.put("1", "１");
			m.put("2", "２");
			m.put("3", "３");
			m.put("4", "４");
			m.put("5", "５");
			m.put("6", "６");
			m.put("7", "７");
			m.put("8", "８");
			m.put("9", "９");
			m.put("a", "ａ");
			m.put("b", "ｂ");
			m.put("c", "ｃ");
			m.put("d", "ｄ");
			m.put("e", "ｅ");
			m.put("f", "ｆ");
			m.put("g", "ｇ");
			m.put("h", "ｈ");
			m.put("i", "ｉ");
			m.put("j", "ｊ");
			m.put("k", "ｋ");
			m.put("l", "ｌ");
			m.put("m", "ｍ");
			m.put("n", "ｎ");
			m.put("o", "ｏ");
			m.put("p", "ｐ");
			m.put("q", "ｑ");
			m.put("r", "ｒ");
			m.put("s", "ｓ");
			m.put("t", "ｔ");
			m.put("u", "ｕ");
			m.put("v", "ｖ");
			m.put("w", "ｗ");
			m.put("x", "ｘ");
			m.put("y", "ｙ");
			m.put("z", "ｚ");
			m.put("A", "Ａ");
			m.put("B", "Ｂ");
			m.put("C", "Ｃ");
			m.put("D", "Ｄ");
			m.put("E", "Ｅ");
			m.put("F", "Ｆ");
			m.put("G", "Ｇ");
			m.put("H", "Ｈ");
			m.put("I", "Ｉ");
			m.put("J", "Ｊ");
			m.put("K", "Ｋ");
			m.put("L", "Ｌ");
			m.put("M", "Ｍ");
			m.put("N", "Ｎ");
			m.put("O", "Ｏ");
			m.put("P", "Ｐ");
			m.put("Q", "Ｑ");
			m.put("R", "Ｒ");
			m.put("S", "Ｓ");
			m.put("T", "Ｔ");
			m.put("U", "Ｕ");
			m.put("V", "Ｖ");
			m.put("W", "Ｗ");
			m.put("X", "Ｘ");
			m.put("Y", "Ｙ");
			m.put("Z", "Ｚ");
			for (Entry<String, String> e : m.entrySet()) {
				hmCharTbl.put(e.getValue(), e.getKey());

			}
		}

		hmCharTbl.put("ｱ", "ア");
		hmCharTbl.put("ｲ", "イ");
		hmCharTbl.put("ｳ", "ウ");
		hmCharTbl.put("ｴ", "エ");
		hmCharTbl.put("ｵ", "オ");
		hmCharTbl.put("ｶ", "カ");
		hmCharTbl.put("ｷ", "キ");
		hmCharTbl.put("ｸ", "ク");
		hmCharTbl.put("ｹ", "ケ");
		hmCharTbl.put("ｺ", "コ");
		hmCharTbl.put("ｻ", "サ");
		hmCharTbl.put("ｼ", "シ");
		hmCharTbl.put("ｽ", "ス");
		hmCharTbl.put("ｾ", "セ");
		hmCharTbl.put("ｿ", "ソ");
		hmCharTbl.put("ﾀ", "タ");
		hmCharTbl.put("ﾁ", "チ");
		hmCharTbl.put("ﾂ", "ツ");
		hmCharTbl.put("ﾃ", "テ");
		hmCharTbl.put("ﾄ", "ト");
		hmCharTbl.put("ﾅ", "ナ");
		hmCharTbl.put("ﾆ", "ニ");
		hmCharTbl.put("ﾇ", "ヌ");
		hmCharTbl.put("ﾈ", "ネ");
		hmCharTbl.put("ﾉ", "ノ");
		hmCharTbl.put("ﾊ", "ハ");
		hmCharTbl.put("ﾋ", "ヒ");
		hmCharTbl.put("ﾌ", "フ");
		hmCharTbl.put("ﾍ", "ヘ");
		hmCharTbl.put("ﾎ", "ホ");
		hmCharTbl.put("ﾏ", "マ");
		hmCharTbl.put("ﾐ", "ミ");
		hmCharTbl.put("ﾑ", "ム");
		hmCharTbl.put("ﾒ", "メ");
		hmCharTbl.put("ﾓ", "モ");
		hmCharTbl.put("ﾔ", "ヤ");
		hmCharTbl.put("ﾕ", "ユ");
		hmCharTbl.put("ﾖ", "ヨ");
		hmCharTbl.put("ﾗ", "ラ");
		hmCharTbl.put("ﾘ", "リ");
		hmCharTbl.put("ﾙ", "ル");
		hmCharTbl.put("ﾚ", "レ");
		hmCharTbl.put("ﾛ", "ロ");
		hmCharTbl.put("ﾜ", "ワ");
		hmCharTbl.put("ｦ", "ヲ");
		hmCharTbl.put("ﾝ", "ン");
		hmCharTbl.put("ｧ", "ァ");
		hmCharTbl.put("ｨ", "ィ");
		hmCharTbl.put("ｩ", "ゥ");
		hmCharTbl.put("ｪ", "ェ");
		hmCharTbl.put("ｫ", "ォ");
		hmCharTbl.put("ｯ", "ッ");
		hmCharTbl.put("ｬ", "ャ");
		hmCharTbl.put("ｭ", "ュ");
		hmCharTbl.put("ｮ", "ョ");
		hmCharTbl.put("ｰ", "ー");
		hmCharTbl.put("｡", "。");
		hmCharTbl.put("､", "、");
		hmCharTbl.put("･", "・");
		hmCharTbl.put("｢", "「");
		hmCharTbl.put("｣", "」");
		hmCharTbl.put("ﾞ", "゛");
		hmCharTbl.put("ﾟ", "゜");
		hmCharTbl.put("ｳﾞ", "ヴ");
		hmCharTbl.put("ｶﾞ", "ガ");
		hmCharTbl.put("ｷﾞ", "ギ");
		hmCharTbl.put("ｸﾞ", "グ");
		hmCharTbl.put("ｹﾞ", "ゲ");
		hmCharTbl.put("ｺﾞ", "ゴ");
		hmCharTbl.put("ｻﾞ", "ザ");
		hmCharTbl.put("ｼﾞ", "ジ");
		hmCharTbl.put("ｽﾞ", "ズ");
		hmCharTbl.put("ｾﾞ", "ゼ");
		hmCharTbl.put("ｿﾞ", "ゾ");
		hmCharTbl.put("ﾀﾞ", "ダ");
		hmCharTbl.put("ﾁﾞ", "ヂ");
		hmCharTbl.put("ﾂﾞ", "ヅ");
		hmCharTbl.put("ﾃﾞ", "デ");
		hmCharTbl.put("ﾄﾞ", "ド");
		hmCharTbl.put("ﾊﾞ", "バ");
		hmCharTbl.put("ﾋﾞ", "ビ");
		hmCharTbl.put("ﾌﾞ", "ブ");
		hmCharTbl.put("ﾍﾞ", "ベ");
		hmCharTbl.put("ﾎﾞ", "ボ");
		hmCharTbl.put("ﾊﾟ", "パ");
		hmCharTbl.put("ﾋﾟ", "ピ");
		hmCharTbl.put("ﾌﾟ", "プ");
		hmCharTbl.put("ﾍﾟ", "ペ");
		hmCharTbl.put("ﾎﾟ", "ポ");

		hmCharTbl.put("　", " ");
		hmCharTbl.put("\t", "  ");
		hmCharTbl.put("（", "(");
		hmCharTbl.put("）", ")");
		hmCharTbl.put("-", "ー");

		//面倒なので、フォルダ、ファイル名に使えない文字も省く
		hmCharTbl.put("/", "_");
		hmCharTbl.put("\\\\", "_");
		hmCharTbl.put("！", "!");
		hmCharTbl.put(" ", "");
		hmCharTbl.put("　", "");

	}

	/**
	 * ノーマライズします。
	 * ディレクトリに変換できない文字も、ノーマライズ対象にしてしまいます。
	 * @param str
	 * @return
	 */
	public static String normalizer(String str) {

		String normalize = java.text.Normalizer.normalize(str,
				java.text.Normalizer.Form.NFKC);

		//パフォーマンス的にはひどいが、他にもひどい所があるので無視する。
		//画像系、プロセス起動のコストに比べればマシ
		for (Entry<String, String> e : hmCharTbl.entrySet()) {
			normalize = normalize.replaceAll(e.getKey(), e.getValue());

		}

		return normalize;

	}
}
