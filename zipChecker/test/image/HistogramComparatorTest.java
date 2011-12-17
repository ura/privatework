package image;

import image.compare.SimpleHistogramComparator;
import junit.framework.TestCase;

public class HistogramComparatorTest extends TestCase {

	public void testComparateHistogramHistogram() {

		String img1 = "./testdata/img/imgcompare/0006-Fate-tohsaka-rin6_small.jpg";
		String img2 = "./testdata/img/imgcompare/0006-Fate-tohsaka-rin6.jpg";
		String img3 = "./testdata/img/imgcompare/0033-Fate-saber83.jpg";
		String img4 = "./testdata/img/imgcompare/0006-Fate-tohsaka-rin6_test1.jpg";
		String img5 = "./testdata/img/imgcompare/0043-20090219235059.jpg";


		Img i1 = new Img(img1);
		Img i2 = new Img(img2);
		Img i3 = new Img(img3);
		Img i4 = new Img(img4);
		Img i5 = new Img(img5);

		i1.createHistogram();
		i2.createHistogram();
		i3.createHistogram();
		i4.createHistogram();
		i5.createHistogram();


		{

			int result = new SimpleHistogramComparator().comparate(i1.getHistogram(),
					i2.getHistogram());
			System.out.println("サイズ違い："+Math.sqrt( result));
		}
		{
			int result = new SimpleHistogramComparator().comparate(i1.getHistogram(),
					i1.getHistogram());
			System.out.println("同一："+Math.sqrt( result));
		}
		{
			int result = new SimpleHistogramComparator().comparate(i2.getHistogram(),
					i3.getHistogram());
			System.out.println("同系色　異なる絵:"+Math.sqrt( result));
		}
		{
			int result = new SimpleHistogramComparator().comparate(i2.getHistogram(),
					i4.getHistogram());
			System.out.println("グレー加工:"+Math.sqrt( result));
		}
		{
			int result = new SimpleHistogramComparator().comparate(i2.getHistogram(),
					i5.getHistogram());
			System.out.println("違う色:"+Math.sqrt( result));
		}
	}

}
