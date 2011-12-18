package image;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.StaticUtil;

public class TreeSaveTest extends TestCase {

	private static Logger log = LoggerFactory.getLogger(TreeSaveTest.class);

	public void testSave() {
		List<Img> allImage;

		{
			String src = "./testdata/img/imgcompare";

			ImageDataTree tree = new ImageDataTree(0, "root", src);
			tree.walk();
			tree.save();

		}
		{
			String src = "D:/Data/wall";
			ImageDataTree tree = new ImageDataTree(0, "root", src);
			tree.walk();
			tree.save();

		}

	}

	public void testLoad() {
		List<Img> allImage;

		{
			log.info("LOADTEST");
			String src = "./testdata/img/imgcompare";

			ImageDataTree tree = new ImageDataTree(0, "root", src);
			tree = tree.load();

			//view(tree);

			allImage = tree.getAllImage();
			//ImgUtil.viewList(allImage);

			tree.log();

		}

	}

	public void testLoad2() {
		List<Img> allImage;

		{
			log.info("LOADTEST");
			String src = "D:/Data/wall";

			ImageDataTree tree = new ImageDataTree(0, "root", src);
			tree = tree.load();
			List<Img> manyImage = tree.getAllImage();

			//view(tree);

			String src2 = "./testdata/img/imgcompare";

			ImageDataTree tree2 = new ImageDataTree(0, "root", src2);
			tree2 = tree2.load();
			allImage = tree2.getAllImage();
			//ImgUtil.viewList(allImage);

			log.info("�f�[�^�̉摜���F{}", allImage.size());

			//�e�X�g�摜�ƁA�ǎ��t�H���_�̔�r�E�E�E�E
			for (Img img : allImage) {
				Collection<Img> list = tree.check(img, 100);
				if (list.size() > 0) {
					ImgUtil.viewList(img, list);
				}

			}

			int percent=10;
			log.info("�S�f�[�^��茟�؂��J�n���܂��B�T���v���f�[�^��:{}% �f�[�^��:{}",percent,manyImage.size());
			//�ǎ��t�H���_����A�����_���ɉ摜���o���A���Ă���摜�����邩���݃`�F�b�N
			for (Img img : manyImage) {

				if (StaticUtil.easyRandom(percent)) {
					Collection<Img> list = tree.check(img, 1000);

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

	private static void view(ImageDataTree tree) {
		for (Map.Entry<String, ImageDataTree> e : tree.getMap().entrySet()) {
			String k = e.getKey();
			ImageDataTree t = e.getValue();
			List<Img> list = t.getList();

			if (list.size() != 1 && list.size() < 10) {

				ImgUtil.viewList(list);

			}

			view(t);

		}

	}
}
