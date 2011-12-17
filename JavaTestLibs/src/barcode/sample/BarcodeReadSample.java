package barcode.sample;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
	public static void main(String[] args) {
		read("result\\mycode.png");

		read("mysample\\0001.jpg");
		read("mysample\\0001-only.jpg");
		read("mysample\\0001-02.jpg");
		read("mysample\\0001-03.jpg");
		read("mysample\\0001-04.jpg");
		read("mysample\\0001-05.jpg");

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

}