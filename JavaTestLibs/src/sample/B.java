package sample;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class B {
	public static void main(String[] args) {
		// バーコードに埋め込むコンテンツ
		String contents = "マイコミジャーナル: http://journal.mycom.co.jp/";

		// エンコードのための付加情報を設定
		Hashtable<EncodeHintType, Object> encodeHint = new Hashtable<EncodeHintType, Object>();
		encodeHint.put(EncodeHintType.CHARACTER_SET, "shift_jis");
		encodeHint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

		// QRコード用の出力ストリームを作成
		Writer writer = new QRCodeWriter();

		try {
			// エンコードを実行
			BitMatrix bitData = writer.encode(contents, BarcodeFormat.QR_CODE,
					129, 129, encodeHint);
			// ファイルに出力
			FileOutputStream output = new FileOutputStream("result\\mycode.png");
			MatrixToImageWriter.writeToStream(bitData, "png", output);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (WriterException ex) {
			ex.printStackTrace();
		}
	}
}