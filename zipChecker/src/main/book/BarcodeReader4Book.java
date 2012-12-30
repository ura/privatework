package book;

import image.HSV;
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

import javax.imageio.ImageIO;

import log.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.ThreadPoolExecutorSync;
import util.file.filter.FileNameFilter;
import util.file.filter.FileNameFilter.MODE;
import collection.Tuple;

import com.google.common.collect.ArrayListMultimap;
import com.google.inject.Inject;
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

import conf.ConfConst;

/**
 * 縦になっているバーコードがあるので、回転を検討する。
 *
 *
 * @author name
 *
 */
public class BarcodeReader4Book {

	private static Logger log = LoggerFactory
			.getLogger(BarcodeReader4Book.class);

	private static final int MAX_DIV = 8;

	private static final int THREAD_BARCODE = ConfConst.MAIN_CONF
			.getInt(ConfConst.THREAD_BARCODE);
	private static final int THREAD_BARCODE_DERAY = ConfConst.MAIN_CONF
			.getInt(ConfConst.THREAD_BARCODE_DERAY);

	@Inject
	private SmillaEnlargerWrapper wrapper;

	public String autoReadDir(File dir) {

		log.info("バーコード抽出処理を開始します。{} ", dir.getAbsolutePath());
		File[] files = dir.listFiles(new FileNameFilter(MODE.EXT_INCLUDE,
				"jpg", "jpeg", "png"));
		if (files.length == 0) {
			return null;
		}

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
	 * ファイル名であまりない長さのファイル名のファイルを返す。
	 * ファイル名が規則性を持っている場合、イリーガルなファイル名を抜き出す。
	 * （表紙、おまけなど。）
	 * 初期値は８で、8以下の数しか無いファイルを返す。
	 * @param asList
	 * @return
	 */
	private List<File> fewLengthFile(List<File> asList) {

		ArrayListMultimap<Integer, File> map = ArrayListMultimap.create();
		for (File file : asList) {
			int length = file.getName().length();
			map.put(length, file);
		}

		ArrayList<File> arrayList = new ArrayList<>();
		for (Integer key : map.keys()) {
			if (map.get(key).size() < 8) {
				arrayList.addAll(map.get(key));
			}

		}

		return arrayList;

	}

	/**
	 * TODO 優先度に応じて多重配列化する
	 * @param asList
	 * @return
	 */
	private List<File> readFileList(List<File> asList) {
		int param = 8;

		List<File> l = new ArrayList<>();
		List<File> l2 = new ArrayList<>();

		List<File> mid = fewLengthFile(asList);
		l.addAll(mid);

		for (File file : asList) {

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

	public class Task<Strings> implements Callable<String> {

		public Task(int i, boolean retry, File file) {
			super();
			this.retry = retry;
			this.file = file;
			this.i = i;
		}

		private boolean retry;
		private File file;
		private int i;

		@Override
		public String toString() {
			return "バーコード読み取りTask [retry=" + retry + ", file="
					+ file.getAbsolutePath() + "]";
		}

		@Override
		public String call() throws Exception {
			return readImpl(retry, file, i);
		}
	}

	private ThreadPoolExecutorSync ex = new ThreadPoolExecutorSync(
			THREAD_BARCODE, THREAD_BARCODE_DERAY);

	protected String read(List<File> asList, boolean retry) {

		//最初と最後にしか、バーコードはついていないと推定する

		List<Callable<String>> list = new ArrayList<>();

		List<File> fileList = readFileList(asList);

		int i = 0;
		for (File file : fileList) {
			Task<String> task = new Task<String>(++i, retry, file);
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
	public String autoRead(String src, final int div) {

		log.debug(src);
		try {
			// 画像を読み込んでビットマップデータを生成
			BufferedImage image = ImageIO.read(new File(src));

			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

			List<Rect> createRect = createRect(bitmap, div);

			String decode = decode(Tuple.newT(bitmap, src), createRect);

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
	protected BinaryBitmap crop(BinaryBitmap bitmap, Rect rect) {

		return bitmap.crop(rect.getLeft(), rect.getTop(), rect.getWidth(),
				rect.getHeight());
	}

	/**
	 * 指定された粒度で、分割用のパラメータを作成する。
	 * デフォルト分割、６まで上がる。
	 * @param bitmap
	 * @param div
	 * @return
	 */
	protected List<Rect> createRect(BinaryBitmap bitmap, int div) {
		List<Rect> set = new ArrayList<BarcodeReader4Book.Rect>();

		int c = 0;
		for (; div <= MAX_DIV; div++) {

			//暫定対策：縦の方の分解率を上げ、二段バーコードの上を取りやすくする。
			// 縦　２～６＊３分割。横２～６分割
			for (int j = 0; j < div * 3; j++) {
				for (int i = 0; i < div; i++) {
					set.add(new Rect(i, j, div, div * 3

					, bitmap.getWidth(), bitmap.getHeight(), c++

					));
				}
			}

			// 縦　２～６＊３分割。横２～６分割
			for (int j = 0; j < div * 6; j++) {
				for (int i = 0; i < div; i++) {
					set.add(new Rect(i, j, div, div * 6, bitmap.getWidth(),
							bitmap.getHeight(), c++));
				}
			}

			//T0DO 分析のために整理
			//			for (int j = 0; j < div * 12; j++) {
			//				for (int i = 0; i < div; i++) {
			//					set.add(new Rect(i, j, div, div * 12, bitmap.getWidth(),
			//							bitmap.getHeight(), c++));
			//				}
			//			}
			//
			//			for (int j = 0; j < div * 24; j++) {
			//				for (int i = 0; i < div; i++) {
			//					set.add(new Rect(i, j, div, div * 24, bitmap.getWidth(),
			//							bitmap.getHeight(), c++));
			//				}
			//			}

		}

		return set;
	}

	/**
	 * 分割して読み取りを試みます。
	 * 画像の１分を定義するクラスです。
	 * @author poti
	 *
	 */
	public static class Rect {

		private int baseWidth;
		private int baseHeight;
		private int top;
		private int left;
		private int widthMax;
		private int heightMax;
		private int count;

		public Rect(int left, int top, int widthMax, int heightMax,
				int baseWidth, int baseHeight, int count) {
			super();
			this.top = top;
			this.left = left;
			this.widthMax = widthMax;
			this.heightMax = heightMax;

			this.baseHeight = baseHeight;
			this.baseWidth = baseWidth;
			this.count = count;
		}

		public int getLeft() {
			return getWidth() * left;
		}

		public int getTop() {
			return getHeight() * top;
		}

		public int getWidth() {
			return baseWidth / widthMax;
		}

		public int getHeight() {
			return baseHeight / heightMax;
		}

		@Override
		public String toString() {
			return "Rect [top=" + getTop() + ", left=" + getLeft()
					+ ", height=" + getHeight() + ", width=" + getWidth() + "]"
					+ "[" + baseHeight + ":" + baseWidth + "]" + "[" + top
					+ ":" + left + ":" + widthMax + ":" + heightMax + "]";
		}

		public String getStaticInfo() {
			return "Rect [" + 20 * baseHeight / baseWidth + "][top=" + top
					+ ", left=" + left + ", widthMax=" + widthMax
					+ ", heightMax=" + heightMax + "]" + count + ":";
		}

	}

	protected String decode(Tuple<BinaryBitmap, String> img, List<Rect> set) {

		//Reader reader = new MultiFormatReader();

		//高速化のために内容変更
		Reader reader = new EAN13Reader();

		String temp = null;

		for (Rect rect : set) {

			String r = x(img, reader, rect);
			if (r != null && r.startsWith("978")) {
				return r;
			}
			if (r != null) {
				temp = r;
			}

		}
		return temp;
	}

	protected String x(Tuple<BinaryBitmap, String> img, Reader reader, Rect rect) {
		BinaryBitmap bitmap = crop(img.val1, rect);

		// デコードを実行
		Result result;
		try {
			//バーコードが見つからない場合はエラー
			result = reader.decode(bitmap);
			// フォーマットを取得
			BarcodeFormat format = result.getBarcodeFormat();
			log.debug("フォ－マット: " + format);
			// コンテンツを取得
			String text = result.getText();
			log.debug("テキスト: " + text);

			if (!text.startsWith("978")) {
				log.info("目的外バーコードのため、スキップします。{}", text);
				log.info(Log.STATIC, "NG:RECT:{}[{}]", rect.getStaticInfo());
				log.info("{}", rect);
				ResultPoint[] points = result.getResultPoints();

				for (int i = 0; i < points.length; i++) {
					log.info("    Point[" + i + "] = " + points[i]);
				}

				return text;
			} else {
				ResultPoint[] points = result.getResultPoints();

				log.info(Log.STATIC, "OK:RECT:{}[{}]", rect.getStaticInfo(),
						img.val2);

				log.debug("位置検出パターン／アライメントパターンの座標: ");
				for (int i = 0; i < points.length; i++) {
					log.debug("    Point[" + i + "] = " + points[i]);
				}
				return text;
			}

			/*
			// 位置検出パターンおよびアラインメントパターンの座標を取得
			ResultPoint[] points = result.getResultPoints();

			log.debug("位置検出パターン／アライメントパターンの座標: ");
			for (int i = 0; i < points.length; i++) {
				log.debug("    Point[" + i + "] = " + points[i]);
			}

			if (format.toString().equals("EAN_13")) {
				return text;
			}*/

		} catch (NotFoundException | ChecksumException | FormatException
				| ArrayIndexOutOfBoundsException e) {
			//見つからないことは普通にある。そのために探しまくっている
			log.debug(Log.STATIC, "NG:RECT:{}", rect.getStaticInfo());
		}
		return null;
	}

	protected String readImpl(boolean retry, File file, int i) {
		String barcord = null;
		try {
			if (retry) {
				log.info("高精細化を目論みます。{}", file.getAbsolutePath());
				File tempFile = wrapper.convertTempFile(file, 200);
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

				List<SmillaEnlargerConf> list = wrapper.convertConfList(file,
						200);
				for (SmillaEnlargerConf smillaEnlargerConf : list) {

					File tempFile = wrapper.convertTempFile(file,
							smillaEnlargerConf);
					barcord = autoRead(tempFile.getAbsolutePath(), 2);
					tempFile.delete();

					if (barcord != null && barcord.startsWith("978")) {
						log.info("バーコード検出 IDX=" + i + "\t"
								+ file.getAbsolutePath() + "\t" + barcord);
						log.info(Log.STATIC, "OK:SmillaEnlargerConf:{}",
								smillaEnlargerConf);

						return barcord;
					} else {
						log.info("不正なバーコードです IDX=" + i + "\t"
								+ file.getAbsolutePath() + "\t" + barcord);
						log.info(Log.STATIC, "NG:SmillaEnlargerConf:{}",
								smillaEnlargerConf);
						barcord = null;
					}

				}

			}
		} catch (Exception e) {
			log.warn("バーコード読み取り時にエラー:{}", file.getAbsoluteFile(), e);
		}
		return barcord;
	}

	public SmillaEnlargerWrapper getWrapper() {
		return wrapper;
	}

	public void setWrapper(SmillaEnlargerWrapper wrapper) {
		this.wrapper = wrapper;
	}
}