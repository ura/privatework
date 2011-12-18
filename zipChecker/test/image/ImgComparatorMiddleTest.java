package image;

import image.compare.ImgComparatorMiddle;

import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.StaticUtil;

public class ImgComparatorMiddleTest extends TestCase {

	private static Logger log = LoggerFactory.getLogger(ImgComparatorMiddleTest.class);



	public void testImgComparatorMiddle() {
		List<Img> allImage;

		{
			log.info("LOADTEST");

			String src = "D:/Data/wall";
			ImageDataTree tree = new ImageDataTree(0, "root", src);
			tree = tree.load();
			List<Img> manyImage = tree.getAllImage();


			String src2 = "./testdata/img/imgcompare";
			ImageDataTree tree2 = new ImageDataTree(0, "root", src2);
			tree2 = tree2.load();
			allImage = tree2.getAllImage();

			log.info("�f�[�^�̉摜���F{}", allImage.size());

			//�e�X�g�摜�ƁA�ǎ��t�H���_�̔�r�E�E�E�E
//			for (Img img : allImage) {
//				Collection<Img> list = tree.check(img, 100,new ImgComparatorMiddle());
//				if (list.size() > 0) {
//					ImgUtil.viewList(img, list);
//				}
//			}

			int percent=10;
			log.info("�S�f�[�^��茟�؂��J�n���܂��B�T���v���f�[�^��:{}% �f�[�^��:{}",percent,manyImage.size());
			//�ǎ��t�H���_����A�����_���ɉ摜���o���A���Ă���摜�����邩���݃`�F�b�N
			for (Img img : manyImage) {

				if (StaticUtil.easyRandom(percent)) {
					Collection<Img> list = tree.check(img, 1000,new ImgComparatorMiddle(50,2000));

					if (list.size() > 0) {
						ImgUtil.viewList(img, list);
					}else{
						log.info("��v�摜�͂���܂���ł����B{}",img.getInfo());
					}
				}else{
					System.out.print(".");
				}

			}

		}

	}


}
