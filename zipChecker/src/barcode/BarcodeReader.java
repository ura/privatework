package barcode;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.file.filter.FileNameFilter;
import util.file.filter.FileNameFilter.MODE;

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

public class BarcodeReader {

	private static Logger log = LoggerFactory.getLogger(BarcodeReader.class);

	private static final int MAX_DIV = 5;

	public static String autoReadDir(File dir) {

		File[] files = dir.listFiles(new FileNameFilter(MODE.EXT_INCLUDE,
				"jpg", "jpeg"));
		List<File> asList = Arrays.asList(files);
		Collections.sort(asList);

		int param = 5;

		//最初と最後にしか、バーコードはついていないと推定する
		for (int i = 0; i < asList.size(); i++) {

			if (i < param || (asList.size() - param) < i) {
				File file = asList.get(i);
				String barcord = autoRead(file.getAbsolutePath(), 3);
				if (barcord != null) {
					log.info("バーコード検出 IDX=" + i + "\t" + file.getAbsolutePath()
							+ "\t" + barcord);
					return barcord;
				}
			}

		}
		return null;

	}

	/**
	 * バーコードが見つからなかった場合、ある程度分割して検出を試みる。
	 * 見つからなかったらNULL
	 *
	 * @param src
	 * @param div
	 * @return
	 */
	public static String autoRead(String src, final int div) {

		log.info(src);
		try {
			// 画像を読み込んでビットマップデータを生成
			BufferedImage image = ImageIO.read(new File(src));

			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

			List<Rect> createRect = createRect(bitmap, div);

			String decode = decode(bitmap, createRect);

			log.info(src + "\t" + decode);

			if (decode == null && div <= MAX_DIV) {
				decode = autoRead(src, div + 1);
			}

			return decode;

		} catch (IOException ex) {
			log.error(src, ex);
		}

		return null;
	}

	/**
	 * 画像を切る。
	 * @param bitmap
	 * @param rect
	 * @return
	 */
	private static BinaryBitmap crop(BinaryBitmap bitmap, Rect rect) {

		return bitmap.crop(rect.left, rect.top, rect.width, rect.height);
	}

	/**
	 * 指定された粒度で、分割用のパラメータを作成する。
	 * @param bitmap
	 * @param div
	 * @return
	 */
	private static List<Rect> createRect(BinaryBitmap bitmap, int div) {
		List<Rect> set = new ArrayList<BarcodeReader.Rect>();
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

	private static String decode(BinaryBitmap basemap, List<Rect> set) {
		Reader reader = new MultiFormatReader();
		for (Rect rect : set) {
			BinaryBitmap bitmap = crop(basemap, rect);

			// デコードを実行
			Result result;
			try {
				result = reader.decode(bitmap);
				// フォーマットを取得
				BarcodeFormat format = result.getBarcodeFormat();
				log.info("フォ－マット: " + format);
				// コンテンツを取得
				String text = result.getText();

				log.info("テキスト: " + text);

				// 位置検出パターンおよびアラインメントパターンの座標を取得
				ResultPoint[] points = result.getResultPoints();
				log.info("位置検出パターン／アライメントパターンの座標: ");
				for (int i = 0; i < points.length; i++) {
					log.info("    Point[" + i + "] = " + points[i]);
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