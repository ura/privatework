package opencv;

import com.googlecode.javacv.jna.cxcore.IplImage;
import com.googlecode.javacv.jna.cxcore.CvSize.ByValue;

import static com.googlecode.javacv.jna.cxcore.*;
import static com.googlecode.javacv.jna.cv.*;
import static com.googlecode.javacv.jna.highgui.*;
import static com.googlecode.javacv.jna.cvaux.*;

/**
 *
 * ���W�b�N�̓��e�B
 *
 * tempate��img��菬��������B �c���́A�����X�P�[���ƔF������B �тƂ��A��荞�݃~�X�������݁A�^�񒆕�����؂�o��
 *
 *
 *
 */
public class BookStrategy implements CutStrategy {

	@Override
	public IplImage[] cut(IplImage image, IplImage template) {

		// �c���̓t���̃T�C�Y�̂͂��B���͗��\���܂Ŏ�荞�ݓ��̉\������
		// �c�̃X�P�[���𐳂Ƃ��ăT�C�Y���킹�B
		int height = image.height;
		int height2 = template.height;

		if (height > height2) {

			// System.out.println( height2 / (float)height);
			image = OpenCVUtil.resize(image, height2 / (float) height);
		} else {
			// System.out.println(height / height2);
			template = OpenCVUtil.resize(template, height / (float) height2);
		}

		template = OpenCVUtil.cut(template, 0.7, 0.7, 0.5, 0.4);

		OpenCVUtil.showImg(image, 5000, "IMG CHECK");
		OpenCVUtil.showImg(template, 5000, "IMG CHECK");

		int h3 = image.getCvSize().height;
		int w3 = image.getCvSize().width;
		int h4 = template.getCvSize().height;
		int w4 = template.getCvSize().width;

		System.out.println("size:" + h3 + "_" + w3 + " :" + h4 + "_" + w4);

		return new IplImage[] { image, template };
	}
}
