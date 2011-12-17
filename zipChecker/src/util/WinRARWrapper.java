package util;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.file.FileMoveUtil;
import conf.ConfConst;

public class WinRARWrapper {
	private static Logger log = LoggerFactory.getLogger(WinRARWrapper.class);
	/**
	 * WINWARにぱすをとおしておｋ
	 */
	private static final String RAR_EXE = ConfConst.MAIN_CONF
			.getVal(ConfConst.WINRAR_PATH);

	public static void decode(String src, String dest) throws IOException,
			InterruptedException {

		new File(dest).mkdir();

		String cmd = "\"" + RAR_EXE + "\" X  -o+  -IBCK \"" + src + "\" * \""
				+ dest + "\"";

		log.info(cmd);
		Process exec = Runtime.getRuntime().exec(cmd);
		exec.waitFor();
		int exitValue = exec.exitValue();
		if (exitValue != 0) {
			throw new IllegalArgumentException("ファイルが解凍できませんでした");
		}

	}

	public static void decode(File src, File dest) throws IOException,
			InterruptedException {

		decode(src.getAbsolutePath(), dest.getAbsolutePath());

	}

	public static void encode(String src, String destName) throws IOException,
			InterruptedException {

		String cmdZip = "\"" + RAR_EXE + "\" A -R -IBCK -afzip -ep1   \""
				+ destName + "\" \"" + src + "/*\"";

		log.info(cmdZip);

		Process execZip = Runtime.getRuntime().exec(cmdZip);

		execZip.waitFor();
		int exitValue = execZip.exitValue();
		if (exitValue == 0) {
			FileMoveUtil.deleteDir(new File(src));
		}
	}

}
