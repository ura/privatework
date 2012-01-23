package opencv;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 *
 * ロジックの内容。
 *
 * tempateをimgより小さくする。 縦幅は、同じスケールと認識する。 帯とか、取り込みミスを見込み、真ん中部分を切り出す
 *
 *
 *
 */
public class BookStrategy implements CutStrategy {

	@Override
	public IplImage[] cut(IplImage image, IplImage template) {

		// 縦幅はフルのサイズのはず。横は裏表紙まで取り込み等の可能性あり
		// 縦のスケールを正としてサイズあわせ。
		int height = image.height();
		int height2 = template.height();

		if (height > height2) {

			// System.out.println( height2 / (float)height);
			image = OpenCVUtil.resize(image, height2 / (float) height);
		} else {
			// System.out.println(height / height2);
			template = OpenCVUtil.resize(template, height / (float) height2);
		}

		template = OpenCVUtil.cut(template, 0.7, 0.7, 0.5, 0.4);

		OpenCVUtil.myShowImg(image, 1000, "変換元");
		OpenCVUtil.myShowImg(template, 1000, "切り出し後");

		int h3 = image.height();
		int w3 = image.width();
		int h4 = template.height();
		int w4 = template.width();

		System.out.println("size:" + h3 + "_" + w3 + " :" + h4 + "_" + w4);

		return new IplImage[] { image, template };
	}
}
