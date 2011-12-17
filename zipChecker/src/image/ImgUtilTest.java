package image;

import junit.framework.TestCase;

public class ImgUtilTest extends TestCase {

	public void testPositionsIntIntInt() {
		{
			int[][][] array = ImgUtil.positions(1024, 768, 2);

			for (int i = 0; i < array.length; i++) {
				for (int j = 0; j < array[i].length; j++) {
					System.out.println(array[i][j][0] + "\t" + array[i][j][1]
							+ "\t" + array[i][j][2] + "\t" + array[i][j][3]
							+ "\t");

				}
			}
		}

		{
			int[][][] array = ImgUtil.positions(1024, 768, 4);

			for (int i = 0; i < array.length; i++) {
				for (int j = 0; j < array[i].length; j++) {
					System.out.println(array[i][j][0] + "\t" + array[i][j][1]
							+ "\t" + array[i][j][2] + "\t" + array[i][j][3]
							+ "\t");

				}
			}
		}

	}

}
