package opencv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core.CvArr;
import com.googlecode.javacv.cpp.opencv_core.CvFileStorage;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvMatND;
import com.googlecode.javacv.cpp.opencv_core.CvPoint;
import com.googlecode.javacv.cpp.opencv_core.CvPoint2D32f;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc.CvHistogram;

import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import static com.googlecode.javacv.cpp.opencv_core.CV_STORAGE_READ;
import static com.googlecode.javacv.cpp.opencv_core.CV_STORAGE_WRITE;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_32F;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvAttrList;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvMinMaxLoc;
import static com.googlecode.javacv.cpp.opencv_core.cvOpenFileStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvReadByName;
import static com.googlecode.javacv.cpp.opencv_core.cvRectangle;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseFileStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import static com.googlecode.javacv.cpp.opencv_core.cvSplit;
import static com.googlecode.javacv.cpp.opencv_core.cvWrite;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_COMP_CORREL;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_HIST_ARRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCalcBackProjectPatch;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCalcHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCreateHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetRectSubPix;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvResize;

/*
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_calib3d.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;
*/

public class OpenCVUtil {
	private static Logger log = LoggerFactory.getLogger(OpenCVUtil.class);

	//	public static IplImage loadImage(String path) {
	//		IplImage image = cvLoadImage("G:\\バーコドテスト\\CP1_CP1_CP7_第04巻\\1174_2.jpg");
	//		if (image == null) {
	//			throw new IllegalArgumentException(path);
	//		}
	//		return image;
	//
	//	}
	//
	public static void myShowImg(IplImage img) {

		myShowImg(img, 1000, "");
	}

	public static void myShowImg(IplImage img, int time, String name) {
		CanvasFrame canvas = new CanvasFrame(name);
		canvas.showImage(img);

		if (time > 0) {
			sleep((long) time);
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

		CvSize size = cvSize((int) (image.width() * x),
				(int) (image.height() * y));

		IplImage cvCreateImage = cvCreateImage(size, image.depth(), 3);
		cvGetRectSubPix(
				image,
				cvCreateImage,
				new CvPoint2D32f((int) (image.width() * xx), (int) (image
						.height() * yy)));

		return cvCreateImage;
	}

	public static IplImage resize(IplImage image, float f) {

		// TODO 元画像からチャネルの取り方がわからない・・・
		IplImage cvCreateImage = cvCreateImage(
				cvSize((int) (image.width() * f), (int) (image.height() * f)),
				IPL_DEPTH_8U, 3);
		cvResize(image, cvCreateImage, 0);
		return cvCreateImage;

	}

	public static IplImage resize(IplImage image, int width, int height) {
		// TODO 元画像からチャネルの取り方がわからない・・・
		IplImage cvCreateImage = cvCreateImage(cvSize(width, height),
				IPL_DEPTH_8U, 3);
		cvResize(image, cvCreateImage, 0);
		return image;

	}

	//
	//	public static IplImage matchTemplateAndRectangle(IplImage image,
	//			IplImage template) {
	//
	//		return matchTemplateAndRectangle(image, template, new NullStrategy());
	//	}
	//
	//	public static IplImage matchTemplateAndRectangle(IplImage image,
	//			IplImage template, CutStrategy strategy) {
	//
	//		DoubleByReference min_val = new DoubleByReference();
	//		DoubleByReference max_val = new DoubleByReference();
	//
	//		CvPoint min_loc = new CvPoint();
	//		CvPoint max_loc = new CvPoint();
	//
	//		IplImage[] imgArray = strategy.cut(image, template);
	//
	//		image = imgArray[CutStrategy.IMG_IDX];
	//		template = imgArray[CutStrategy.TEMPLATE_IDX];
	//
	//		ByValue size = cvSize(image.width - template.width + 1, image.height
	//				- template.height + 1);
	//
	//		IplImage dest_img = cvCreateImage(size, IPL_DEPTH_32F, 1);
	//		cvMatchTemplate(image, template, dest_img, CV_TM_CCORR_NORMED);
	//
	//		cvMinMaxLoc(dest_img, min_val, max_val, min_loc, max_loc, null);
	//
	//		System.out.println("Max:" + max_val.getValue());
	//
	//		IplImage clone = image.clone();
	//		cvRectangle(
	//				clone,
	//				max_loc.byValue(),
	//				cvPoint(max_loc.x + template.width, max_loc.y + template.height),
	//				CV_RGB(255, 0, 0), 3, 1, 0);
	//
	//		return clone;
	//	}
	//
	private static CvHistogram loadTrainingData() {
		log.info("loading training data");

		CvMat pTrainPersonNumMat = null; // the person numbers during training
		CvFileStorage fileStorage;
		int i;

		// create a file-storage interface
		fileStorage = cvOpenFileStorage("facedata.xml", // filename
				null, // memstorage
				CV_STORAGE_READ, // flags
				null); // encoding
		if (fileStorage == null) {
			log.error("Can't open training database file 'data/facedata.xml'.");
			return null;
		}

		Pointer pointer = cvReadByName(fileStorage, // fs
				null, // map
				"avgTrainImg", // name
				cvAttrList()); // attributes
		return new CvHistogram(pointer);

	}

	private static void storeTrainingData(CvHistogram hist) {
		CvFileStorage fileStorage;
		int i;

		log.info("writing data/facedata.xml");

		// create a file-storage interface
		fileStorage = cvOpenFileStorage("facedata.xml", // filename
				null, // memstorage
				CV_STORAGE_WRITE, // flags
				null); // encoding

		cvWrite(fileStorage, // fs
				"avgTrainImg", // name
				hist, // value
				cvAttrList()); // attributes

		// release the file-storage interface
		cvReleaseFileStorage(fileStorage);
	}

	public static IplImage matchTemplateAndRectangle2(IplImage image,
			IplImage template, CutStrategy strategy) {

		//		  imagename = argc > 1 ? argv[1] : "room7.png";
		//		  templatename = argc > 2 ? argv[2] : "room7_temp.png";
		//		  src_img = cvLoadImage (imagename, CV_LOAD_IMAGE_COLOR);
		//		  tmp_img = cvLoadImage (templatename, CV_LOAD_IMAGE_COLOR);
		//		  if(src_img == 0 || tmp_img == 0)
		//		    return -1;

		CvPoint min_loc = new CvPoint();
		CvPoint max_loc = new CvPoint();

		IplImage[] imgArray = strategy.cut(image, template);

		image = imgArray[CutStrategy.IMG_IDX];
		template = imgArray[CutStrategy.TEMPLATE_IDX];

		//		 // (2)ヒストグラム作成のための色平面バッファを作成します．
		//		  for (i = 0; i < 3; i++) {
		//		    src_planes[i] = cvCreateImage (cvGetSize (src_img), IPL_DEPTH_8U, 1);
		//		    tmp_planes[i] = cvCreateImage (cvGetSize (tmp_img), IPL_DEPTH_8U, 1);
		//		  }

		IplImage[] src_planes = new IplImage[3];
		IplImage[] tmp_planes = new IplImage[3];

		for (int i = 0; i < 3; i++) {

			src_planes[i] = cvCreateImage(cvGetSize(image), IPL_DEPTH_8U, 1);
			tmp_planes[i] = cvCreateImage(cvGetSize(template), IPL_DEPTH_8U, 1);
		}

		//		 // (3)入力画像（探索画像，テンプレート画像）の色空間を，RGBからHSVに変換します．
		//		  src_hsv = cvCreateImage (cvGetSize (src_img), IPL_DEPTH_8U, 3);
		//		  tmp_hsv = cvCreateImage (cvGetSize (tmp_img), IPL_DEPTH_8U, 3);
		//		  cvCvtColor (src_img, src_hsv, CV_BGR2HSV);
		//		  cvCvtColor (tmp_img, tmp_hsv, CV_BGR2HSV);
		//		  cvCvtPixToPlane (src_hsv, src_planes[0], src_planes[1], src_planes[2], 0);
		//		  cvCvtPixToPlane (tmp_hsv, tmp_planes[0], tmp_planes[1], tmp_planes[2], 0);

		IplImage src_hsv = cvCreateImage(cvGetSize(image), IPL_DEPTH_8U, 3);
		IplImage tmp_hsv = cvCreateImage(cvGetSize(template), IPL_DEPTH_8U, 3);
		cvCvtColor(image, src_hsv, CV_BGR2HSV);
		cvCvtColor(template, tmp_hsv, CV_BGR2HSV);
		cvSplit(src_hsv, src_planes[0], src_planes[1], src_planes[2], null);
		cvSplit(tmp_hsv, tmp_planes[0], tmp_planes[1], tmp_planes[2], null);

		//		 int i, hist_size = 90;
		//		  float h_ranges[] = { 0, 180 };
		//		  float *ranges[] = { h_ranges };
		//		  // (4)テンプレート画像の色相平面のヒストグラムを計算します．
		//		  hist = cvCreateHist (1, &hist_size, CV_HIST_ARRAY, ranges, 1);
		//		  cvCalcHist (&tmp_planes[0], hist, 0, 0);

		CvHistogram hist = cvCreateHist(1, new int[] { 90 }, CV_HIST_ARRAY,
				new float[][] { { 0, 180 } }, 1);

		cvCalcHist(tmp_planes, hist, 0, null);

		CvArr bins = hist.bins();
		CvMatND mat = hist.mat();

		storeTrainingData(hist);
		CvHistogram hist2 = loadTrainingData();

		//		  // (5)探索画像全体に対して，テンプレートのヒストグラムとの距離（手法に依存）を計算します．
		//		  dst_size =
		//		    cvSize (src_img->width - tmp_img->width + 1,
		//		            src_img->height - tmp_img->height + 1);
		//		  dst_img = cvCreateImage (dst_size, IPL_DEPTH_32F, 1);
		//		  cvCalcBackProjectPatch (&src_planes[0], dst_img, cvGetSize (tmp_img), hist,
		//		                          CV_COMP_CORREL, 1.0);
		//		  cvMinMaxLoc (dst_img, &min_val, &max_val, &min_loc, &max_loc, NULL);

		//cvCreateHist(int dims, int sizes[], int type, float ranges[][], int uniform)

		CvSize cvSize2 = cvSize(image.width() - template.width() + 1,
				image.height() - template.height() + 1);

		IplImage dest_img = cvCreateImage(cvSize2, IPL_DEPTH_32F, 1);
		cvCalcBackProjectPatch(new IplImage[] { src_planes[0] }, dest_img,
				cvGetSize(template), hist2, CV_COMP_CORREL, 1.0);

		double[] maxVal = new double[1];
		double[] minVal = new double[1];
		//cvMinMaxLoc(CvArr cvarr, double ad[], double ad1[], CvPoint cvpoint, CvPoint cvpoint1, CvArr cvarr1);
		cvMinMaxLoc(dest_img, maxVal, minVal, min_loc, max_loc, null);

		System.out.println("Max:" + maxVal[0]);

		IplImage clone = image.clone();
		cvRectangle(
				clone,
				max_loc,
				cvPoint(max_loc.x() + template.width(),
						max_loc.y() + template.height()), CV_RGB(255, 0, 0), 3,
				1, 0);

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
					IplImage yotuImg = cvLoadImage(yotu);
					IplImage yotuAmaImg = cvLoadImage(yotu_ama[i]);

					myShowImg(yotuImg, 1000, "元");
					myShowImg(yotuAmaImg, 1000, "比較対象");

					IplImage cutImg = matchTemplateAndRectangle2(yotuImg,
							yotuAmaImg, new BookStrategy());

					System.out.println("----------");
					myShowImg(cutImg, 5000, "結果のつもり");

				}

			}

			{
				//				String yotu = "./testdata/img/YXM_no1.png";
				//				String yotu_ama = "./testdata/img/よつばと１_amazon.png";
				//
				//				IplImage yotuImg = cvLoadImage(yotu);
				//				IplImage yotuAmaImg = cvLoadImage(yotu_ama);
				//
				//				showImg(yotuImg);
				//				showImg(yotuAmaImg);

				//				IplImage cutImg = matchTemplateAndRectangle2(yotuImg,
				//						yotuAmaImg, new BookStrategy());
				//
				//				showImg(cutImg);
			}

		} catch (Throwable e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	//	private static void main1() {
	//		String path = "./testdata/img/k1.jpg";
	//		String small = "./testdata/img/small.jpg";
	//
	//		IplImage img = loadImage(path);
	//		IplImage smallImg = loadImage(small);
	//
	//		showImg(img);
	//		showImg(smallImg);
	//
	//		IplImage rectImg = matchTemplateAndRectangle(img, smallImg);
	//		showImg(rectImg);
	//	}
	//
	//	public static void m(String[] arg) {
	//
	//		String[] a = { "1174_2.jpg", "1174_3.jpg", "1174_4.jpg" };
	//
	//		for (String string : a) {
	//			IplImage image = loadImage("G:\\バーコドテスト\\CP1_CP1_CP7_第04巻\\"
	//					+ string);
	//			IplImage src_hsv = cvCreateImage(cvGetSize(image), IPL_DEPTH_8U, 3);
	//			cvCvtColor(image, src_hsv, CV_BGR2HSV);
	//
	//			CvScalar mean = new CvScalar();
	//			CvScalar std_dev = new CvScalar();
	//
	//			cvAvgSdv(image, mean, std_dev, src_hsv);
	//
	//			System.out.println(string);
	//			System.out.println(std_dev.val[0]);
	//			System.out.println(std_dev.val[1]);
	//			System.out.println(std_dev.val[2]);
	//
	//		}
	//
	//	}

	private static void sleep(long l) {

		try {
			Thread.sleep(l);
		} catch (InterruptedException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}