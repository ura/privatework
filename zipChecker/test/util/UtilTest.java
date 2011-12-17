package util;

import junit.framework.TestCase;

public class UtilTest extends TestCase {

	public void testEasyRandomInt() {

		intTest(100);
		intTest(50);
		intTest(10);
		intTest(1);

	}

	public static void intTest(int per){
		int test=1000*1000*10;
		int count=0;
		for (int i = 0; i < test; i++) {

			if(Util.easyRandom(per)){
				count++;
			}

		}

		float result=count*100f/test;

		System.out.println(per+"%  result:"+result );
	}




	public void testEasyRandomFloat() {
		floatTest(100);
		floatTest(50);
		floatTest(10);
		floatTest(1);
		floatTest(0.1f);
		floatTest(0.01f);



	}

	public static void floatTest(float per){
		int test=1000*1000*10;
		int count=0;
		for (int i = 0; i < test; i++) {

			if(Util.easyRandom(per)){
				count++;
			}

		}

		float result=count*100f/test;

		System.out.println(per+"%  result:"+result );
	}
}

