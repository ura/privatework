package zip;

import image.ImgUtil;

import java.io.File;
import java.util.Collection;

import junit.framework.TestCase;

public class ZipJpegHandlerTest extends TestCase {

	public void testHanlde() {

		//handle("./testdata/OK/(C76) (���l��) [Yan-Yam] �^��g�܂݂� (EVA).zip");
		//handle("./testdata/OK/(��ʃR�~�b�N) [�Ƃ�c�݂̂�] ���u���} ��04��.zip");
		handle("./testdata/OK/(��ʃR�~�b�N) [��{�`�s] �V���I�G���@���Q���I�� ��01��-��11���i���j.zip");

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
