package util;

import java.io.File;
import java.io.IOException;

import util.file.FileMoveUtil;

public class WinRARWrapper {

	private static final String RAR_EXE = "C:\\Program Files\\WinRAR\\WinRAR.exe";

	public static void decode(String src, String dest) throws IOException,
			InterruptedException {

		new File(dest).mkdir();

		String cmd = "\"" + RAR_EXE + "\" X  -o+  -IBCK \"" + src + "\" * \""
				+ dest + "\"";

		System.out.println(cmd);
		Process exec = Runtime.getRuntime().exec(cmd);
		exec.waitFor();
		int exitValue = exec.exitValue();
		if (exitValue != 0) {
			throw new IllegalArgumentException("ƒtƒ@ƒCƒ‹‚ª‰ð“€‚Å‚«‚Ü‚¹‚ñ‚Å‚µ‚½");
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

		System.out.println(cmdZip);

		Process execZip = Runtime.getRuntime().exec(cmdZip);

		execZip.waitFor();
		int exitValue = execZip.exitValue();
		if (exitValue == 0) {
			FileMoveUtil.deleteDir(new File(src));
		}
	}

}
