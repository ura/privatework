package image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import conf.ConfConst;
import static util.file.FileNameUtil.getExt;

public class SmillaEnlargerWrapper {

	private static final String EXE = ConfConst.MAIN_CONF
			.getVal(ConfConst.SmillaEnlargerCL_PATH);
	private static Logger log = LoggerFactory
			.getLogger(SmillaEnlargerWrapper.class);

	//C:\Users\poti\Downloads\SmillaEnlarger-0.9.0\SmillaEnlarger\SmillaEnlargerCL.exe E:\workcomic\JPG\1_1324222630058\06\A002.jpg -z 200 -o E:\workcomic\JPG\1_1324222630058\06\A002-1.jpg -sharp 100 -flat 0 -dither 10 -deNoise 19 -preSharp 19 -fNoise 0

	public static boolean convert(String src, String dest, int per) {
		return convert(new File(src), new File(dest), per);
	}

	public static File convertTempFile(File src, int per) throws IOException {

		File tempFile = File.createTempFile("img", "." + getExt(src));

		convert(src, tempFile, per);
		return tempFile;

	}

	public static boolean convert(File src, File dest, int per) {

		try {
			String cmd = "\""
					+ EXE
					+ "\"  \""
					+ src.getAbsolutePath()
					+ "\" -o \""
					+ dest.getAbsolutePath()
					+ "\"   -sharp 100 -flat 0 -dither 10 -deNoise 19 -preSharp 19 -fNoise 0    -z  "
					+ per;

			log.info(cmd);
			log.info(src.exists() + "\t" + dest.exists());

			final Process exec = Runtime.getRuntime().exec(cmd);

			try (InputStream in = exec.getInputStream();
					InputStream in2 = exec.getErrorStream();
					OutputStream o = exec.getOutputStream();) {

				//結果を読み捨てる必要あり
				int i = exec.getInputStream().read();
				while (i != -1) {
					i = exec.getInputStream().read();
				}

				exec.waitFor();

				int exitValue = exec.exitValue();

				if (exitValue != 0) {
					log.error("ERROR:" + cmd);

					return false;
				} else {
					return true;
				}
			}
		} catch (IOException | InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return false;

	}
}
