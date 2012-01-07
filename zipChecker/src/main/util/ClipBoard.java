package util;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static util.StaticUtil.sleep;

public class ClipBoard {

	public static String getClipboard() {

		String now = loadClipboard();
		while (true) {
			String temp = loadClipboard();
			if (!now.equals(temp) && !temp.equals("")) {
				return temp;
			}

			sleep(50);
		}

	}

	/**
	 * 現在のクリップボードの状態を取得する
	 * @return
	 */
	public static String loadClipboard() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

		String str = "";
		try {
			Transferable object = clipboard.getContents(null);
			str = (String) object.getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e) {

		} catch (IOException e) {

		} catch (Exception e) {

		}
		return str;
	}
}
