package image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HSV {

	private static Logger log = LoggerFactory.getLogger(HSV.class);

	public static boolean isColar(File filename) {

		double hsvStd = hsvAvgS(filename);

		if (0.02d > hsvStd) {

			log.info("カラーと判定しました。{}:{}", filename.getAbsolutePath(), hsvStd);
			return false;

		} else {
			return true;
		}

	}

	//Image画像のRGB値を求めて格納するメソッド
	public static double hsvAvgS(File filename) {

		BufferedImage image = null;
		try {
			image = ImageIO.read(filename);
		} catch (IOException e1) {
			throw new IllegalArgumentException(e1);
		}

		int width = image.getWidth();
		int height = image.getHeight();

		Color color;

		int[] rgb = new int[width * height];
		double[] hue = new double[width * height];
		double[] sat = new double[width * height];

		//img画像の一次元RGB配列を得る
		PixelGrabber grabber = new PixelGrabber(image, 0, 0, width, height,
				rgb, 0, width);
		try {
			grabber.grabPixels();
		} catch (InterruptedException e) {
		}

		float[] hsbvals = new float[3];

		for (int i = 0; i < width * height; i++) {

			color = new Color(rgb[i]);
			int red = color.getRed();
			int green = color.getGreen();
			int blue = color.getBlue();

			Color.RGBtoHSB(red, green, blue, hsbvals);

			//色相
			hue[i] = (double) hsbvals[0];
			//彩度
			sat[i] = (double) hsbvals[1];
			//明度
			//val[i] = (double) hsbvals[2];
		}

		//System.out.println(getAverage(sat));
		//getStandardDeviation(hue);

		return getAverage(sat);

	}

	public static double getAverage(final double[] scoreArray) {
		float totalNumber = 0;
		for (double i : scoreArray) {
			totalNumber += i;
		}
		return totalNumber / scoreArray.length;
	}

	public static double getUnbiasedVariance(final double[] scoreArray) {
		double variance = 0;
		for (double i : scoreArray) {
			variance += Math.pow(i - getAverage(scoreArray), 2);
		}
		//Unbiased Variance(n - 1) is not  normal variance!(n)
		return variance / (scoreArray.length - 1);
	}

	public static double getStandardDeviation(final double[] scoreArray) {
		//Of course,if you are a good programmer.you may combine getUnbiasedVariance and getStandardDeviation.
		return Math.sqrt(getUnbiasedVariance(scoreArray));

	}

}
