package zip;

import image.ImgUtil;

import java.io.File;
import java.util.Collection;

import junit.framework.TestCase;

public class ZipJpegHandlerTest extends TestCase {

	public void testHanlde() {

		//handle("./testdata/OK/(C76) (同人誌) [Yan-Yam] 真希波まみれ (EVA).zip");
		//handle("./testdata/OK/(一般コミック) [とよ田みのる] ラブロマ 第04巻.zip");
		handle("./testdata/OK/(一般コミック) [貞本義行] 新世紀エヴァンゲリオン 第01巻-第11巻（完）.zip");

	}

	private void handle(String path) {
		ZipJpegHandler handler = new ZipJpegHandler(ZipJpegHandler.COLLECT_MODE.COLOR);
		new ZipUtil().handle(new File(path),

		handler);

		ImgUtil.viewList(handler.getImgList());

		Collection<String> list = handler.getEntryList();
		for (String string : list) {
			System.out.println(string);
		}
		getHandle(path, list);


	}

	private void getHandle(String path,Collection<String> list) {
		ZipGetHandler handler = new ZipGetHandler(list);
		new ZipUtil().handle(new File(path),

		handler);





	}

}
