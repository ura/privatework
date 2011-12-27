package book;

import image.HSV;
import image.SmillaEnlargerWrapper;
import image.SmillaEnlargerWrapper.SmillaEnlargerConf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.ThreadPoolExecutorSync;
import util.file.filter.FileNameFilter;
import util.file.filter.FileNameFilter.MODE;
import collection.MapList;

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

/**
 * 縦になっているバーコードがあるので、回転を検討する。
 *
 *
 * @author name
 *
 */
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

	/**
	 * ファイル名でもっとも、多い長さを返す。
	 * @param asList
	 * @return
	 */
	private static int mid(List<File> asList) {

		MapList<Integer, File> map = new MapList<>();
		for (File file : asList) {
			int length = file.getName().length();
			map.put(length, file);
		}
		int maxCount = 0;
		int result = 0;
		for (Entry<Integer, Collection<File>> e : map.entrySet()) {
			Integer key = e.getKey();
			int count = e.getValue().size();

			if (count > maxCount) {
				maxCount = count;
				result = key.intValue();
			}

		}

		return result;

	}

	/**
	 * TODO 優先度に応じて多重配列化する
	 * @param asList
	 * @return
	 */
	private static List<File> readFileList(List<File> asList) {
		int param = 8;

		List<File> l = new ArrayList<>();
		List<File> l2 = new ArrayList<>();
		int mid = mid(asList);

		for (File file : asList) {
			//ファイル名の長さが最頻値と違う場合
			if (file.getName().length() != mid && !l.contains(file)
					&& !l2.add(file)) {
				if (HSV.isColar(file)) {
					l.add(file);
				} else {
					l2.add(file);
				}
			}
			//ルールが変なものも表紙かも
			if (file.getName().matches("[0-9]+[a-zA-Z]{1}\\.[a-zA-Z]+")
					&& !l.contains(file) && !l2.add(file)) {
				if (HSV.isColar(file)) {
					l.add(file);
				} else {
					l2.add(file);
				}
			}
		}

		for (int i = 0; i < asList.size(); i++) {
			File file = asList.get(i);
			if ((i < param || (asList.size() - param) < i) && !l.contains(file)
					&& !l2.add(file)) {
				if (HSV.isColar(file)) {
					l.add(file);
				} else {
					l2.add(file);
				}
			}
		}

		l.addAll(l2);

		log.info("バーコード処理候補です。候補数　{}", l.size());
		for (File file : l) {
			log.info("バーコード処理候補です。{}", file.getAbsolutePath());
		}

		return l;
	}

	private static class Task<Strings> implements Callable<String> {

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
		public String toString() {
			return "バーコード読み取りTask [retry=" + retry + ", file="
					+ file.getAbsolutePath() + "]";
		}

		@Override
		public String call() throws Exception {
			String barcord = null;
			try {
				if (retry) {
					log.info("高精細化を目論みます。{}", file.getAbsolutePath());
					File tempFile = SmillaEnlargerWrapper.convertTempFile(file,
							200);
					barcord = autoRead(tempFile.getAbsolutePath(), 2);
					//barcord = autoRead(file.getAbsolutePath(), 2);
					tempFile.delete();

				} else {
					barcord = autoRead(file.getAbsolutePath(), 2);
				}

				//書籍には、二段のバーコードがあり、上のバーコードがほしい
				if (barcord != null) {
					log.info("バーコード検出 IDX=" + i + "\t" + file.getAbsolutePath()
							+ "\t" + barcord);
					if (barcord.startsWith("978")) {
						log.info("書籍バーコード検出 IDX=" + i + "\t"
								+ file.getAbsolutePath() + "\t" + barcord);
						return barcord;
					}

					if (barcord.startsWith("192")) {
						log.info("求めているバーコードではないため、スキップします。 IDX=" + i + "\t"
								+ file.getAbsolutePath() + "\t" + barcord);

					} else if (!barcord.startsWith("978")) {

						log.info("バーコードの読取エラーと想定されます。スキップします。 IDX=" + i + "\t"
								+ file.getAbsolutePath() + "\t" + barcord);

					}

					List<SmillaEnlargerConf> list = SmillaEnlargerWrapper
							.convertConfList(file, 200);
					for (SmillaEnlargerConf smillaEnlargerConf : list) {
						File tempFile = SmillaEnlargerWrapper.convertTempFile(
								file, smillaEnlargerConf);
						barcord = autoRead(tempFile.getAbsolutePath(), 2);
						tempFile.delete();

						if (barcord != null && barcord.startsWith("978")) {
							log.info("バーコード検出 IDX=" + i + "\t"
									+ file.getAbsolutePath() + "\t" + barcord);
							return barcord;
						} else {
							log.info("不正なバーコードです IDX=" + i + "\t"
									+ file.getAbsolutePath() + "\t" + barcord);
						}

					}

				}
			} catch (Exception e) {
				log.warn("バーコード読み取り時にエラー:{}", file.getAbsoluteFile(), e);
			}
			return barcord;
		}
	}

	private static ThreadPoolExecutorSync ex = new ThreadPoolExecutorSync(10,
			200);

	private static String read(List<File> asList, boolean retry) {

		//最初と最後にしか、バーコードはついていないと推定する

		List<Callable<String>> list = new ArrayList<>();

		List<File> fileList = readFileList(asList);

		int i = 0;
		for (File file : fileList) {
			Task task = new Task(++i, retry, file);
			list.add(task);

		}

		String string = ex.invokeAll(list);
		if (string != null) {
			return string;
		} else {
			log.info("バーコードが取得できませんでした。親フォルダサンプル：{}  MODE:{}", asList.get(0)
					.getParent(), retry);
			return null;
		}

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

		for (; div <= MAX_DIV; div++) {
			for (int i = 0; i < div; i++) {
				//暫定対策：縦の方の分解率を上げ、二段バーコードの上を取りやすくする。
				for (int j = 0; j < div * 3; j++) {

					set.add(new Rect(bitmap.getWidth() * i / div, bitmap
							.getHeight() * j / div / 3,
							bitmap.getWidth() / div, bitmap.getHeight() / div
									/ 3));
				}
				for (int j = 0; j < div * 2; j++) {

					set.add(new Rect(bitmap.getWidth() * i / div, bitmap
							.getHeight() * j / div / 2,
							bitmap.getWidth() / div, bitmap.getHeight() / div
									/ 2));
				}
				for (int j = 0; j < div * 6; j++) {

					set.add(new Rect(bitmap.getWidth() * i / div, bitmap
							.getHeight() * j / div / 6,
							bitmap.getWidth() / div, bitmap.getHeight() / div
									/ 6));
				}
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

		String temp = null;
		for (Rect rect : set) {
			BinaryBitmap bitmap = crop(basemap, rect);

			// デコードを実行
			Result result;
			try {
				result = reader.decode(bitmap);
				// フォーマットを取得
				BarcodeFormat format = result.getBarcodeFormat();
				log.debug("フォ－マット: " + format);
				// コンテンツを取得
				String text = result.getText();
				log.debug("テキスト: " + text);
				if (!text.startsWith("978")) {
					log.info("目的外バーコードのため、スキップします。{}", text);
					temp = text;
					continue;
				}

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
		return temp;
	}
}