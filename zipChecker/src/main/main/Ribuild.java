package main;

import java.util.List;

import util.UserInput;
import book.BookFileUtil;

public class Ribuild {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		List<String> i1 = UserInput.getUserInputsSwing(
				"パスを入力( デフォルト　L:\\DATA\\COMIC)", 1);
		if (i1.size() == 0) {
			i1.add("L:\\DATA\\COMIC");
		}

		List<String> i3 = UserInput.getUserInputsSwing("ファイル名を入力", 5);

		BookFileUtil.rebuildArcWithUI(i1.get(0),
				i3.toArray(new String[i3.size()]));
		System.exit(0);

	}

}
