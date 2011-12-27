package main;

import java.util.List;

import book.BookFileUtil;

import util.UserInput;

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

		List<String> i2 = UserInput.getUserInputsSwing("正式名を入力", 1);
		List<String> i3 = UserInput.getUserInputsSwing("ファイル名を入力", 5);

		BookFileUtil.rebuildArcWithUI(i1.get(0), i2.get(0),
				i3.toArray(new String[i3.size()]));
		System.exit(0);

	}

}
