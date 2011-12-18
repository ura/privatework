package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
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
	private static final String WORK_DIR = ConfConst.MAIN_CONF
			.getVal(ConfConst.ARC_WORK_DIR);

	public static void decode(String src, String dest) throws IOException,
			InterruptedException {

		decode(new File(src), new File(dest));

	}

	private static boolean decodeCore(File src, File dest) throws IOException,
			InterruptedException {
		dest.mkdir();

		String cmd = "\"" + RAR_EXE + "\" X  -o+  -IBCK -inul \""
				+ src.getAbsolutePath() + "\" * \"" + dest.getAbsolutePath()
				+ "\"";

		log.info(cmd);
		log.info(src.exists() + "\t" + dest.exists());

		final Process exec = Runtime.getRuntime().exec(cmd);

		exec.waitFor();
		int exitValue = exec.exitValue();

		if (exitValue != 0) {
			log.error("ERROR:" + cmd);

			return false;
		} else {
			return true;
		}
	}

	public static void decode(File src, File dest) throws IOException,
			InterruptedException {

		boolean b = decodeCore(src, dest);

		if (!b) {
			File work1 = new File(FileMoveUtil.createTempDir(WORK_DIR));
			File temp = new File(work1.getAbsolutePath() + File.separator
					+ "temp.rar");

			Files.copy(src.toPath(), temp.toPath());

			File workDest = new File(FileMoveUtil.createTempDir(WORK_DIR));

			boolean workResult = decodeCore(temp, workDest);
			FileMoveUtil.moveParent(workDest, "zip", "rar", "jpeg", "jpg",
					"png");

			temp.delete();
			work1.delete();

			FileUtils.copyDirectory(workDest, dest);
			workDest.delete();

			if (!workResult) {
				throw new IllegalStateException();
			}

		}

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
			//FileMoveUtil.deleteDir(new File(src));
		}
	}

}
