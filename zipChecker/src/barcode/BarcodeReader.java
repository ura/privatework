package barcode;

import image.SmillaEnlargerWrapper;
import image.SmillaEnlargerWrapper.SmillaEnlargerConf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.oned.EAN13Reader;

import static util.StaticUtil.sleep;

public class BarcodeReader {

	private static Logger log = LoggerFactory.getLogger(BarcodeReader.class);

	private static final int MAX_DIV = 6;

	public static String autoReadDir(File dir) {
		log.info("バーコード抽出処理を開始します。{} ", dir.getAbsolutePath());
		File[] files = dir.listFiles(new FileNameFilter(MODE.EXT_INCLUDE,
				"jpg", "jpeg"));
		List<File> asList = Arrays.asList(files);
		for (File f : asList) {
			log.debug("バーコード抽出一次対象：{}", f);
		}

		Collections.sort(asList);

		String bar = read(asList, false);
		if (bar != null) {
			return bar;
		}
		bar = read(asList, true);
		if (bar != null) {
			return bar;
		}
		//リトライをかける
		//画像を変換して再チャレンジ

		log.warn("バーコード非検出 " + "\t" + dir.getAbsolutePath());

		return null;

	}

	private static boolean isReadFile(int idx, List<File> asList) {
		int param = 8;

		File file = asList.get(idx);

		if (file.getName().matches("[0-9]+[a-zA-Z]{1}\\.[a-zA-Z]+")) {
			log.info("XX " + file.getName());

			return true;
		}

		return idx < param || (asList.size() - param) < idx;
	}

	private static class Task implements Callable<String> {

		public Task(int i, boolean retry, File file) {
			super();
			this.retry = retry;
			this.file = file;
			this.i = i;
		}

		private boolean retry;
		private File file;
		int i;

		@Override
		public String call() throws Exception {
			String barcord = null;
			try {
				if (retry) {
					log.info("高精細化を目論みます。{}", file.getAbsolutePath());
					//File tempFile = SmillaEnlargerWrapper.convertTempFile(file,
					//		200);
					//barcord = autoRead(tempFile.getAbsolutePath(), 2);
					barcord = autoRead(file.getAbsolutePath(), 2);
					//tempFile.delete();
				} else {
					barcord = autoRead(file.getAbsolutePath(), 2);
				}

				//書籍には、二段のバーコードがあり、上のバーコードがほしい
				if (barcord != null) {
					log.info("バーコード検出 IDX=" + i + "\t" + file.getAbsolutePath()
							+ "\t" + barcord);
					if (barcord.startsWith("192")) {
						log.info("求めているバーコードではないため、スキップします。 IDX=" + i + "\t"
								+ file.getAbsolutePath() + "\t" + barcord);

					} else if (!barcord.startsWith("978")) {
						log.info("バーコードの読取エラーと想定されます。スキップします。 IDX=" + i + "\t"
								+ file.getAbsolutePath() + "\t" + barcord);

						List<SmillaEnlargerConf> list = SmillaEnlargerWrapper
								.convertConfList(file, 200);
						for (SmillaEnlargerConf smillaEnlargerConf : list) {
							File tempFile = SmillaEnlargerWrapper
									.convertTempFile(file, smillaEnlargerConf);
							barcord = autoRead(tempFile.getAbsolutePath(), 2);
							tempFile.delete();

							if (barcord != null && barcord.startsWith("978")) {
								log.info("バーコード検出 IDX=" + i + "\t"
										+ file.getAbsolutePath() + "\t"
										+ barcord);
								return barcord;
							} else {
								log.info("不正なバーコードです IDX=" + i + "\t"
										+ file.getAbsolutePath() + "\t"
										+ barcord);
							}

						}

					}

				}
			} catch (Exception e) {
				log.warn("バーコード読み取り時にエラー:{}", file.getAbsoluteFile(), e);
			}
			return barcord;
		}
	}

	private static ExecutorService ex = Executors.newFixedThreadPool(5);

	private static String read(List<File> asList, boolean retry) {

		try {
			//最初と最後にしか、バーコードはついていないと推定する

			List<Future<String>> list = new ArrayList<>();

			for (int i = 0; i < asList.size(); i++) {
				if (isReadFile(i, asList)) {

					File file = asList.get(i);
					Future<String> submit = ex.submit(new Task(i, retry, file));
					list.add(submit);

				}
			}
			ex.shutdown();
			try {
				String barcode;
				while (!ex.isTerminated()) {

					barcode = getTaskResult(list);
					if (barcode != null) {
						return barcode;
					}

					sleep(500l);
				}
				barcode = getTaskResult(list);
				if (barcode != null) {
					return barcode;
				}
			} finally {
				ex.shutdownNow();

			}

		} catch (InterruptedException e) {
			log.info("シャットダウンの中止と思われるInterruptedExceptionが発生。{}",
					e.getMessage());

		} catch (ExecutionException e) {
			log.error("想定外のエラー", e);
		}
		log.info("バーコードが取得できませんでした。親フォルダサンプル：{}  MODE:{}", asList.get(0)
				.getParent(), retry);
		return null;

	}

	private static String getTaskResult(List<Future<String>> list)
			throws InterruptedException, ExecutionException {
		String barcode = null;
		for (Future<String> future : list) {
			if (future.isDone()) {
				barcode = future.get();

				if (barcode != null) {
					log.info("バーコードを戻します:{}", barcode);
					return barcode;
				}
			}

		}
		return barcode;
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

		log.debug(src);
		try {
			// 画像を読み込んでビットマップデータを生成
			BufferedImage image = ImageIO.read(new File(src));

			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

			List<Rect> createRect = createRect(bitmap, div);

			String decode = decode(bitmap, createRect);

			log.debug(src + "\t" + decode);

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

		for (int i = 0; i < div; i++) {
			//暫定対策：縦の方の分解率を上げ、二段バーコードの上を取りやすくする。
			for (int j = 0; j < div * 3; j++) {

				set.add(new Rect(bitmap.getWidth() * i / div, bitmap
						.getHeight() * j / div / 3, bitmap.getWidth() / div,
						bitmap.getHeight() / div / 3));
			}
			for (int j = 0; j < div * 2; j++) {

				set.add(new Rect(bitmap.getWidth() * i / div, bitmap
						.getHeight() * j / div / 2, bitmap.getWidth() / div,
						bitmap.getHeight() / div / 2));
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

		//Reader reader = new MultiFormatReader();

		//高速化のために内容変更
		Reader reader = new EAN13Reader();

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

				log.debug("位置検出パターン／アライメントパターンの座標: ");
				for (int i = 0; i < points.length; i++) {
					log.debug("    Point[" + i + "] = " + points[i]);
				}

				if (format.toString().equals("EAN_13")) {
					return text;
				}

			} catch (NotFoundException | ChecksumException | FormatException
					| ArrayIndexOutOfBoundsException e) {
				//見つからないことは普通にある。そのために探しまくっている

			}

		}
		return null;
	}
}