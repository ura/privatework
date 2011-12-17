package image;

import java.util.ArrayList;
import java.util.Collection;

public class ImgUtil {

	public static final int WIDTH = 0;
	public static final int HEIGHT = 1;

	public static final int START_WIDTH = 0;
	public static final int START_HEIGHT = 1;
	public static final int BLOCK_WIDTH = 2;
	public static final int BLOCK_HEIGHT = 3;

	public static int position(int w, int h, int wMax, int hMax, int divide) {

		int x = wMax / w;
		int y = hMax / h;

		return x + x * y;

	}

	public static int[] positions(int w, int h, int wMax, int hMax, int divide) {

		int x = divide * w / wMax;
		int y = divide * h / hMax;

		return new int[] { x, y };

	}

	public static int[][][] positions(int wMax, int hMax, int divide) {

		int[][][] array = new int[divide][divide][4];

		int wBlock = wMax / divide;
		int hBlock = hMax / divide;

		for (int i = 0; i < divide; i++) {
			for (int j = 0; j < divide; j++) {

				int[] rect=new int[]{wBlock*i,hBlock*j,wBlock,hBlock};
				array[i][j]=rect;

			}

		}

		return  array;

	}

	public static  void view( Img img){
		ArrayList<Img> list = new ArrayList<Img>();
		list.add(img);
		new ImgFrame().viewStop(list);

	}
	public static  void viewList( Collection<Img> list){
		new ImgFrame().viewStop(list);

	}

	public static  void viewList(Img img, Collection<Img> list){
		ArrayList<Img> list2 = new ArrayList<Img>();
		list2.add(img);
		list2.addAll(list);

		new ImgFrame().viewStop(list2);

	}

	public static  void viewList(Img img, Collection<Img> list, Collection<Img> list2){
		ArrayList<Img> l = new ArrayList<Img>();
		l.add(img);
		l.addAll(list);

		new ImgFrame().viewStop(l,list2);

	}


}
