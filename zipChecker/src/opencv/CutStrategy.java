package opencv;

import com.googlecode.javacv.jna.cxcore.IplImage;

public interface CutStrategy {

	public static final int IMG_IDX = 0;
	public static final int TEMPLATE_IDX = 1;

	IplImage[] cut(IplImage image, IplImage template);

}
