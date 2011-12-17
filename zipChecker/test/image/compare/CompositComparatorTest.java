package image.compare;

import image.ImageDataTree;
import image.Img;
import image.ImgUtil;

import java.util.List;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.Util;

public class CompositComparatorTest extends TestCase {

	private static Logger log = LoggerFactory.getLogger(CompositComparatorTest.class);

	public void testCompositComparatorTest() {
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



			int percent=10;
			log.info("�S�f�[�^��茟�؂��J�n���܂��B�T���v���f�[�^��:{}% �f�[�^��:{}",percent,manyImage.size());
			//�ǎ��t�H���_����A�����_���ɉ摜���o���A���Ă���摜�����邩���݃`�F�b�N
			for (Img img : manyImage) {

				if (Util.easyRandom(percent)) {
					CompositComparator comparator = new CompositComparator(new ImgComparatorMiddle(50,2000),300,2000);

					CompositComparator result = tree.check(img, comparator);
					result.printInfo(img);

					if (result.getSameList().size() > 0) {

						if(result.getSameList().size()==1 && result.getNearList().size() ==0){

						}else{
							ImgUtil.viewList(img, result.getSameList(),result.getNearList());
						}

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
