package barcode.sample;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class BarcodeReadSample {
	private static final int MAX_DIV = 5;

	public static void main(String[] args) {

		read("mysample\\0001.jpg");
		read("mysample\\0001-only.jpg");
		read("mysample\\0001-02.jpg");
		read("mysample\\0001-03.jpg");
		read("mysample\\0001-04.jpg");
		read("mysample\\0001-05.jpg");

		autoRead("mysample\\0001.jpg", 2);
		autoRead("mysample\\0001.jpg", 3);
		autoRead("mysample\\0001.jpg", 4);

		readDir("mysample\\hyoushi");
	}

	public static void read(String src) {
		// マルチフォーマット対応の入力ストリームを生成
		Reader reader = new MultiFormatReader();

		System.out.println();
		System.out.println(src);
		try {
			// 画像を読み込んでビットマップデータを生成
			BufferedImage image = ImageIO.read(new File(src));

			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

			// デコードを実行
			Result result = reader.decode(bitmap);

			// フォーマットを取得
			BarcodeFormat format = result.getBarcodeFormat();
			System.out.println("フォ－マット: " + format);
			// コンテンツを取得
			String text = result.getText();
			System.out.println("テキスト: " + text);

			// 位置検出パターンおよびアラインメントパターンの座標を取得
			ResultPoint[] points = result.getResultPoints();
			System.out.println("位置検出パターン／アライメントパターンの座標: ");
			for (int i = 0; i < points.length; i++) {
				System.out.println("    Point[" + i + "] = " + points[i]);
			}
		} catch (NotFoundException ex) {
			ex.printStackTrace(System.out);
		} catch (ChecksumException ex) {
			ex.printStackTrace(System.out);
		} catch (FormatException ex) {
			ex.printStackTrace(System.out);
		} catch (IOException ex) {
			ex.printStackTrace(System.out);
		}
	}

	public static void readDir(String dir) {

		File f = new File(dir);
		File[] listFiles = f.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				return name.endsWith(".jpeg") || name.endsWith(".jpg");
			}
		});

		for (File file : listFiles) {

			autoRead(file.getAbsolutePath(), 3);

		}

	}

	public static String autoRead(String src, final int div) {

		System.out.println();
		System.out.println(src);
		try {
			// 画像を読み込んでビットマップデータを生成
			BufferedImage image = ImageIO.read(new File(src));

			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

			Set<Rect> createRect = createRect(bitmap, div);

			String decode = decode(bitmap, createRect);

			System.out.println(src + "\t" + decode);

			if (decode == null && div <= MAX_DIV) {
				decode = autoRead(src, div + 1);
			}

			return decode;

		} catch (IOException ex) {
			ex.printStackTrace(System.out);
		}

		return "";
	}

	public static BinaryBitmap crop(BinaryBitmap bitmap, Rect rect) {

		return bitmap.crop(rect.left, rect.top, rect.width, rect.height);
	}

	public static Set<Rect> createRect(BinaryBitmap bitmap, int div) {
		Set<Rect> set = new HashSet<BarcodeReadSample.Rect>();
		System.out.println("Base  " + bitmap.getWidth() + ":"
				+ bitmap.getHeight());
		for (int i = 0; i < div; i++) {
			for (int j = 0; j < div; j++) {

				/*
				 * System.out.println(bitmap.getWidth() * i / div + ":" +
				 * bitmap.getHeight() * j / div + ":" + bitmap.getWidth() * (i +
				 * 1) / div + ":" + bitmap.getHeight() * (j + 1) / div);
				 */

				set.add(new Rect(bitmap.getWidth() * i / div, bitmap
						.getHeight() * j / div, bitmap.getWidth() / div, bitmap
						.getHeight() / div));
			}

		}
		return set;
	}

	public static class Rect {

		public Rect(int left, int top, int width, int height) {
			super();
			this.top = top;
			this.left = left;
			this.width = width;
			this.height = height;
		}

		public int top;
		public int left;
		public int width;
		public int height;

		@Override
		public String toString() {
			return "Rect [top=" + top + ", left=" + left + ", width=" + width
					+ ", height=" + height + "]";
		}

	}

	private static String decode(BinaryBitmap basemap, Set<Rect> set) {
		Reader reader = new MultiFormatReader();
		for (Rect rect : set) {
			BinaryBitmap bitmap = crop(basemap, rect);

			// デコードを実行
			Result result;
			try {
				result = reader.decode(bitmap);
				// フォーマットを取得
				BarcodeFormat format = result.getBarcodeFormat();
				System.out.println("フォ－マット: " + format);
				// コンテンツを取得
				String text = result.getText();

				System.out.println("テキスト: " + text);

				// 位置検出パターンおよびアラインメントパターンの座標を取得
				ResultPoint[] points = result.getResultPoints();
				System.out.println("位置検出パターン／アライメントパターンの座標: ");
				for (int i = 0; i < points.length; i++) {
					System.out.println("    Point[" + i + "] = " + points[i]);
				}

				if (format.toString().equals("EAN_13")) {
					return text;
				}

			} catch (NotFoundException e) {
				// TODO 自動生成された catch ブロック
				// e.printStackTrace();
			} catch (ChecksumException e) {
				// TODO 自動生成された catch ブロック
				// e.printStackTrace();
			} catch (FormatException e) {
				// TODO 自動生成された catch ブロック
				// e.printStackTrace();
			}

		}

		// TODO
		return null;
	}

}