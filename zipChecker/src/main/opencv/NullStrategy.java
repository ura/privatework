package opencv;

import com.googlecode.javacv.cpp.opencv_core.IplImage;

/**
 *

 *
 */
public class NullStrategy implements CutStrategy {

	@Override
	public IplImage[] cut(IplImage image, IplImage template) {

		return new IplImage[] { image, template };
	}

}
