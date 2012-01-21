package util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Normalizer {
	private static Map<String, String> hmCharTbl = new HashMap<String, String>();
	static {
		{
			Map<String, String> m = new HashMap<String, String>();
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
			m.put("a", "Ａ");
			m.put("b", "Ｂ");
			m.put("c", "Ｃ");
			m.put("d", "Ｄ");
			m.put("e", "Ｅ");
			m.put("f", "Ｆ");
			m.put("g", "Ｇ");
			m.put("h", "Ｈ");
			m.put("i", "Ｉ");
			m.put("j", "Ｊ");
			m.put("k", "Ｋ");
			m.put("l", "Ｌ");
			m.put("m", "Ｍ");
			m.put("n", "Ｎ");
			m.put("o", "Ｏ");
			m.put("p", "Ｐ");
			m.put("q", "Ｑ");
			m.put("r", "Ｒ");
			m.put("s", "Ｓ");
			m.put("t", "Ｔ");
			m.put("u", "Ｕ");
			m.put("v", "Ｖ");
			m.put("w", "Ｗ");
			m.put("x", "Ｘ");
			m.put("y", "Ｙ");
			m.put("z", "Ｚ");
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

		hmCharTbl.put("①", "1");
		hmCharTbl.put("②", "2");
		hmCharTbl.put("③", "3");
		hmCharTbl.put("④", "4");
		hmCharTbl.put("⑤", "5");
		hmCharTbl.put("⑥", "6");
		hmCharTbl.put("⑦", "7");
		hmCharTbl.put("⑧", "8");
		hmCharTbl.put("⑨", "9");

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

	public static boolean equals(String str1, String str2) {

		return normalizer(str1).equals(normalizer(str2));

	}

	/**
	 * ノーマライズした上で判定。
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean contain(String str1, String str2) {

		return normalizer(str1).contains(normalizer(str2));

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

		return normalize.toLowerCase();

	}
}
