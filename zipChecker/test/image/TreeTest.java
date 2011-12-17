package image;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class TreeTest extends TestCase {

	public void testWalk() {
		List<Img> allImage;

		{
			String src = "./testdata/img/imgcompare";

			ImageDataTree tree = new ImageDataTree(0, "root",src);
			tree.walk();

			tree.log();
			view(tree);

			allImage = tree.getAllImage();
			ImgUtil.viewList(allImage);
		}
		{
			String src = "D:/Data/wall";
			// src ="D:/Data/wall/good";
			ImageDataTree tree = new ImageDataTree(0, "root",src);
			tree.walk();

			tree.save();

			tree.log();
			//view(tree);

			for (Img img : allImage) {
				tree.check(img,1000);
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
