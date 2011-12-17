package opencv;

import static com.googlecode.javacv.jna.cxcore.*;
import static com.googlecode.javacv.jna.cv.*;
import static com.googlecode.javacv.jna.highgui.*;
import static com.googlecode.javacv.jna.cvaux.*;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.jna.cxcore.CvMat;
import com.googlecode.javacv.jna.cxcore.CvPoint2D32f;
import com.googlecode.javacv.jna.cxcore.CvRect;
import com.googlecode.javacv.jna.cxcore.IplImage;
import com.googlecode.javacv.jna.cxcore.CvSize.ByValue;
import com.sun.jna.ptr.DoubleByReference;

public class OpenCVUtil {

	public static IplImage loadImage(String path) {
		IplImage image = cvLoadImage(path, CV_LOAD_IMAGE_COLOR);
		if (image == null) {
			throw new IllegalArgumentException(path);
		}
		return image;

	}

	public static void showImg(IplImage img) {

		showImg(img, 1000, "");
	}

	public static void showImg(IplImage img, int time, String name) {
		CanvasFrame canvas = new CanvasFrame(name);
		canvas.showImage(img);

		if (time > 0) {
			sleep((long) time);
		} else {
			cvWaitKey(-1);

		}
		canvas.dispose();

	}

	/**
	 * イメージを矩形で抜き出す。
	 *
	 * @param image
	 * @param x
	 *            抜き出す範囲(0～1.0)
	 * @param y
	 *            抜き出す範囲(0～1.0)
	 * @param xx
	 *            中心点の指定
	 * @param yy
	 *            中心点の指定
	 * @return
	 */
	public static IplImage cut(IplImage image, double x, double y, double xx,
			double yy) {

		ByValue size = cvSize((int) (image.width * x), (int) (image.height * y));

		IplImage cvCreateImage = cvCreateImage(size, image.depth, 3);
		cvGetRectSubPix(image, cvCreateImage, new CvPoint2D32f(
				(int) (image.width * xx), (int) (image.height * yy)).byValue());

		return cvCreateImage;
	}

	public static IplImage resize(IplImage image, float f) {

		// TODO 元画像からチャネルの取り方がわからない・・・
		IplImage cvCreateImage = cvCreateImage(new ByValue(
				(int) (image.width * f), (int) (image.height * f)),
				image.depth, 3);
		cvResize(image, cvCreateImage, 0);
		return cvCreateImage;

	}

	public static IplImage resize(IplImage image, int width, int height) {
		// TODO 元画像からチャネルの取り方がわからない・・・
		IplImage cvCreateImage = cvCreateImage(new ByValue(width, height),
				image.depth, 3);
		cvResize(image, cvCreateImage, 0);
		return image;

	}

	public static IplImage matchTemplateAndRectangle(IplImage image,
			IplImage template) {

		return matchTemplateAndRectangle(image, template, new NullStrategy());
	}

	public static IplImage matchTemplateAndRectangle(IplImage image,
			IplImage template, CutStrategy strategy) {

		DoubleByReference min_val = new DoubleByReference();
		DoubleByReference max_val = new DoubleByReference();

		CvPoint min_loc = new CvPoint();
		CvPoint max_loc = new CvPoint();

		IplImage[] imgArray = strategy.cut(image, template);

		image = imgArray[CutStrategy.IMG_IDX];
		template = imgArray[CutStrategy.TEMPLATE_IDX];

		ByValue size = cvSize(image.width - template.width + 1, image.height
				- template.height + 1);

		IplImage dest_img = cvCreateImage(size, IPL_DEPTH_32F, 1);
		cvMatchTemplate(image, template, dest_img, CV_TM_CCORR_NORMED);

		cvMinMaxLoc(dest_img, min_val, max_val, min_loc, max_loc, null);

		System.out.println("Max:" + max_val.getValue());

		IplImage clone = image.clone();
		cvRectangle(
				clone,
				max_loc.byValue(),
				cvPoint(max_loc.x + template.width, max_loc.y + template.height),
				CV_RGB(255, 0, 0), 3, 1, 0);

		return clone;
	}

	public static IplImage matchTemplateAndRectangle2(IplImage image,
			IplImage template, CutStrategy strategy) {

		DoubleByReference min_val = new DoubleByReference();
		DoubleByReference max_val = new DoubleByReference();

		CvPoint min_loc = new CvPoint();
		CvPoint max_loc = new CvPoint();

		IplImage[] imgArray = strategy.cut(image, template);

		image = imgArray[CutStrategy.IMG_IDX];
		template = imgArray[CutStrategy.TEMPLATE_IDX];

		IplImage[] src_planes = new IplImage[4];
		IplImage[] tmp_planes = new IplImage[4];

		for (int i = 0; i < 4; i++) {
			src_planes[i] = cvCreateImage(cvGetSize(image), IPL_DEPTH_8U, 1);
			tmp_planes[i] = cvCreateImage(cvGetSize(template), IPL_DEPTH_8U, 1);
		}

		IplImage src_hsv = cvCreateImage(cvGetSize(image), IPL_DEPTH_8U, 3);
		IplImage tmp_hsv = cvCreateImage(cvGetSize(template), IPL_DEPTH_8U, 3);
		cvCvtColor(image, src_hsv, CV_BGR2HSV);
		cvCvtColor(template, tmp_hsv, CV_BGR2HSV);

		cvSplit(src_hsv, src_planes[0], src_planes[1], src_planes[2], null);
		cvSplit(tmp_hsv, tmp_planes[0], tmp_planes[1], tmp_planes[2], null);

		FloatPointerByReference f = null;
		CvHistogram hist = cvCreateHist(1, new int[] { 16 }, CV_HIST_ARRAY, f,
				1);

		cvCalcHist(tmp_planes, hist, 0, null);

		ByValue size = cvSize(image.width - template.width + 1, image.height
				- template.height + 1);

		IplImage dest_img = cvCreateImage(size, IPL_DEPTH_32F, 1);
		cvCalcBackProjectPatch(src_planes, dest_img, cvGetSize(template), hist,
				CV_COMP_CORREL, 1.0);

		cvMinMaxLoc(dest_img, min_val, max_val, min_loc, max_loc, null);

		System.out.println("Max:" + max_val.getValue());

		IplImage clone = image.clone();
		cvRectangle(
				clone,
				max_loc.byValue(),
				cvPoint(max_loc.x + template.width, max_loc.y + template.height),
				CV_RGB(255, 0, 0), 3, 1, 0);

		return clone;

	}

	public static void main(String[] args) {
		try {

			// main1();
			/*
			 * { String yotu = "./testdata/img/よつばと１.png"; String yotu_ama =
			 * "./testdata/img/よつばと１_amazon_a.png";
			 *
			 * IplImage yotuImg = loadImage(yotu); IplImage yotuAmaImg =
			 * loadImage(yotu_ama);
			 *
			 * showImg(yotuImg); showImg(yotuAmaImg);
			 *
			 * IplImage cutImg = matchTemplateAndRectangle(yotuImg, yotuAmaImg,
			 * new BookStrategy());
			 *
			 * showImg(cutImg); } { String yotu =
			 * "./testdata/img/YXM_no1_cut.png"; String yotu_ama =
			 * "./testdata/img/YXM_no1_Ama_cut.png";
			 *
			 * IplImage yotuImg = loadImage(yotu); IplImage yotuAmaImg =
			 * loadImage(yotu_ama);
			 *
			 * showImg(yotuImg); showImg(yotuAmaImg);
			 *
			 * IplImage cutImg = matchTemplateAndRectangle(yotuImg, yotuAmaImg,
			 * new BookStrategy());
			 *
			 * showImg(cutImg); }
			 */
			{
				String yotu = "./testdata/img/YXM_no1.png";
				String[] yotu_ama = { "./testdata/img/YXM_no1_Ama.png",
						"./testdata/img/YXM_no10_Ama.png",
						"./testdata/img/YXM_no11_Ama.png" };

				for (int i = 0; i < yotu_ama.length; i++) {
					IplImage yotuImg = loadImage(yotu);
					IplImage yotuAmaImg = loadImage(yotu_ama[i]);

					showImg(yotuImg);
					showImg(yotuAmaImg);

					IplImage cutImg = matchTemplateAndRectangle2(yotuImg,
							yotuAmaImg, new BookStrategy());

					System.out.println("----------");
					showImg(cutImg, 10000, "result");

				}

			}

			{
				String yotu = "./testdata/img/YXM_no1.png";
				String yotu_ama = "./testdata/img/よつばと１_amazon.png";

				IplImage yotuImg = loadImage(yotu);
				IplImage yotuAmaImg = loadImage(yotu_ama);

				showImg(yotuImg);
				showImg(yotuAmaImg);

				IplImage cutImg = matchTemplateAndRectangle2(yotuImg,
						yotuAmaImg, new BookStrategy());

				showImg(cutImg);
			}

		} catch (Throwable e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	private static void main1() {
		String path = "./testdata/img/k1.jpg";
		String small = "./testdata/img/small.jpg";

		IplImage img = loadImage(path);
		IplImage smallImg = loadImage(small);

		showImg(img);
		showImg(smallImg);

		IplImage rectImg = matchTemplateAndRectangle(img, smallImg);
		showImg(rectImg);
	}

	private static void sleep(long l) {

		try {
			Thread.sleep(l);
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}