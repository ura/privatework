package util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.file.FileOperationUtil;
import conf.ConfConst;
import static util.file.FileNameUtil.getExt;

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

	private static String createDecodeCmd(File src, File dest)
			throws IOException, InterruptedException {
		String cmd = "\"" + RAR_EXE + "\" X  -o+  -IBCK -inul \""
				+ src.getAbsolutePath() + "\" * \"" + dest.getAbsolutePath()
				+ "\"";
		return cmd;
	}

	private static boolean decodeCore(File src, File dest) throws IOException,
			InterruptedException {
		dest.mkdir();

		String cmd = createDecodeCmd(src, dest);

		log.info(cmd);

		final Process exec = Runtime.getRuntime().exec(cmd);

		try (InputStream i = exec.getInputStream();
				InputStream i2 = exec.getErrorStream();
				OutputStream o = exec.getOutputStream();) {

			exec.waitFor();
			int exitValue = exec.exitValue();

			if (exitValue != 0) {

				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 *
	 * @param src
	 * @param dest
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void decode(File src, File dest) throws IOException,
			InterruptedException {

		log.info(src.getAbsolutePath() + " >>" + dest.getAbsolutePath());
		boolean b = decodeCore(src, dest);

		if (!b) {
			File work1 = FileOperationUtil.createTempDir(WORK_DIR);

			String ext = getExt(src);

			File temp = new File(work1.getAbsolutePath() + File.separator
					+ "temp." + ext);

			Files.copy(src.toPath(), temp.toPath());

			File workDest = FileOperationUtil.createTempDir(WORK_DIR, "DECODE");

			boolean workResult = decodeCore(temp, workDest);

			temp.delete();
			work1.delete();

			FileUtils.copyDirectory(workDest, dest);

			FileOperationUtil.deleteForce(workDest);

			if (!workResult) {
				log.error("致命的な解凍エラーが発生しました。{}\n{}", src.getAbsolutePath(),
						createDecodeCmd(temp, workDest));
				throw new IllegalStateException("致命的な解凍エラーが発生しました。"
						+ src.getAbsolutePath());
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

	public static void encode(File src, File dest) throws IOException,
			InterruptedException {
		encode(src.getAbsolutePath(), dest.getAbsolutePath());

	}
}
