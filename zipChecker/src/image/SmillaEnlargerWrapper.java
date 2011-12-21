package image;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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
		return convert(new File(src), new File(dest), new SmillaEnlargerConf(
				per));
	}

	public static File convertTempFile(File src, int per) throws IOException {

		File tempFile = File.createTempFile("img", "." + getExt(src));

		convert(src, tempFile, new SmillaEnlargerConf(per));
		return tempFile;

	}

	public static File convertTempFile(File src, SmillaEnlargerConf conf)
			throws IOException {

		File tempFile = File.createTempFile("img", "." + getExt(src));

		convert(src, tempFile, conf);
		return tempFile;

	}

	/**
	 * 様々な設定で変換を試みます。
	 * @param src
	 * @param per
	 * @return
	 * @throws IOException
	 */
	public static List<SmillaEnlargerConf> convertConfList(File src, int per)
			throws IOException {

		List<SmillaEnlargerConf> l = new ArrayList<SmillaEnlargerConf>();
		/*
		 *
		 * デフォルト
		this.sharp = 100;
		this.flat = 0;
		this.dither = 10;
		this.deNoise = 19;
		this.preSharp = 19;
		this.fNoise = 0;

		Sharpness: higher values lead to sharper edges, might look artificial if too sharp
		Flatness: higher values produce more 'painted' looking results with less gradients
		PreSharpen: applies simple sharpening to the source before enlarging
		Dithering: add a slightly analogue looking grain-structure to the result
		DeNoise: remove some noise and artifacts from the source
		FractNoise: just a gimmick: get some irregularity into your result; contours and colors are modified by plasma fractal noise.

		*/

		l.add(new SmillaEnlargerConf(200, 100, 0, 10, 19, 19, 0));
		l.add(new SmillaEnlargerConf(200, 100, 0, 10, 100, 19, 0));

		l.add(new SmillaEnlargerConf(200, 100, 0, 10, 19, 19, 100));
		l.add(new SmillaEnlargerConf(200, 100, 0, 10, 19, 19, 50));

		l.add(new SmillaEnlargerConf(100, 100, 0, 10, 19, 19, 0));
		l.add(new SmillaEnlargerConf(300, 100, 0, 10, 19, 19, 0));

		l.add(new SmillaEnlargerConf(200, 100, 0, 10, 50, 19, 0));
		l.add(new SmillaEnlargerConf(200, 100, 0, 10, 70, 19, 0));

		l.add(new SmillaEnlargerConf(200, 0, 0, 10, 19, 19, 0));
		l.add(new SmillaEnlargerConf(200, 40, 0, 10, 19, 19, 0));
		l.add(new SmillaEnlargerConf(200, 80, 0, 10, 19, 19, 0));

		l.add(new SmillaEnlargerConf(200, 80, 0, 20, 10, 0, 0));
		l.add(new SmillaEnlargerConf(100, 80, 0, 20, 10, 0, 0));

		l.add(new SmillaEnlargerConf(200, 100, 24, 10, 0, 18, 69));
		l.add(new SmillaEnlargerConf(100, 100, 24, 10, 0, 18, 69));
		l.add(new SmillaEnlargerConf(300, 100, 24, 10, 0, 18, 69));

		return l;

	}

	public static class SmillaEnlargerConf {
		public SmillaEnlargerConf(int per, int sharp, int flat, int dither,
				int deNoise, int preSharp, int fNoise) {
			super();
			this.per = per;
			this.sharp = sharp;
			this.flat = flat;
			this.dither = dither;
			this.deNoise = deNoise;
			this.preSharp = preSharp;
			this.fNoise = fNoise;
		}

		public SmillaEnlargerConf(int per) {
			super();
			this.per = per;
			this.sharp = 100;
			this.flat = 0;
			this.dither = 10;
			this.deNoise = 19;
			this.preSharp = 19;
			this.fNoise = 0;
		}

		int per;
		int sharp;
		int flat;
		int dither;
		int deNoise;
		int preSharp;
		int fNoise;
	}

	public static boolean convert(File src, File dest, SmillaEnlargerConf conf) {

		try {

			//パスの問題か、元ファイルのSRCフォルダによりファイルが読み込めない場合がある。
			//よって、とりあえず、コピーしておく。
			File srcCP = File.createTempFile("imgSmillaEnlargerWrapper", "."
					+ getExt(src));
			srcCP.delete();
			Files.copy(src.toPath(), srcCP.toPath());

			String cmd = "\"" + EXE + "\"  \"" + srcCP.getAbsolutePath()
					+ "\" -o \"" + dest.getAbsolutePath() + "\"   -sharp "
					+ conf.sharp + " -flat " + conf.flat + " -dither "
					+ conf.dither + " -deNoise " + conf.deNoise + " -preSharp "
					+ conf.preSharp + " -fNoise " + conf.fNoise + "    -z  "
					+ conf.per;

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
				srcCP.delete();

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
