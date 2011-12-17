package image;

import static image.ImgUtil.BLOCK_HEIGHT;
import static image.ImgUtil.BLOCK_WIDTH;
import static image.ImgUtil.START_HEIGHT;
import static image.ImgUtil.START_WIDTH;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

;

public class Histogram implements Serializable {

	public enum Settings {
		LOW(1, 8), MID(3, 8);

		public final int imgDivide;
		public final int cloorDivide;

		private Settings(int imgDivide, int cloorDivide) {
			this.cloorDivide = cloorDivide;
			this.imgDivide = imgDivide;
		}

	};

	/**
	 *
	 */
	private static final long serialVersionUID = -7712274503635034355L;

	private static Logger log = LoggerFactory.getLogger(Histogram.class);

	private HistogramBlock[][] histogramBlockArray;
	private String comment;
	private final Settings setting;

	public static final int CLOOR_RATE = 10000;

	public Histogram(Settings setting) {
		this.setting = setting;
	}

	public void createHistogram(BufferedImage img) {

		int wMax = img.getWidth();
		int hMax = img.getHeight();

		histogramBlockArray = new HistogramBlock[setting.imgDivide][setting.imgDivide];

		int[][][] wh = ImgUtil.positions(wMax, hMax, setting.imgDivide);

		for (int i = 0; i < setting.imgDivide; i++) {
			for (int j = 0; j < setting.imgDivide; j++) {

				histogramBlockArray[i][j] = new HistogramBlock(img,
						setting.cloorDivide, wh[i][j]);
				histogramBlockArray[i][j].regist();
			}

		}

		for (HistogramBlock[] histCls : histogramBlockArray) {
			for (HistogramBlock histCls2 : histCls) {
				histCls2.cal();
			}
		}

	}

	public void freeMemory() {
		for (HistogramBlock[] histCls : histogramBlockArray) {
			for (HistogramBlock histCls2 : histCls) {
				histCls2.freeMemory();
			}
		}
	}

	public static class CloorBlock implements Serializable {

		/**
		 *
		 */
		private static final long serialVersionUID = -2223909200379765117L;

		private int red;
		private int green;
		private int blue;

		long count = 0;
		int num = 0;

		@Deprecated
		public CloorBlock(int red, int green, int blue) {
			super();
			this.red = red;
			this.green = green;
			this.blue = blue;
		}

		public CloorBlock(int red, int green, int blue, int count) {
			super();
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.count = count;
		}

		public void increment() {
			count++;
		}

		public void setNum(int num) {
			this.num = num;
		}

		public int rate() {

			return (int) (((long) (count)) * CLOOR_RATE / num);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + blue;
			result = prime * result + green;
			result = prime * result + red;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CloorBlock other = (CloorBlock) obj;
			if (blue != other.blue)
				return false;
			if (green != other.green)
				return false;
			if (red != other.red)
				return false;
			return true;
		}

		public void log() {
			if (log.isDebugEnabled()) {
				log.debug("RED {} GREEN{} BLUE{} :{}%:{}", new Object[] {
						this.red, this.green, this.blue, rate(), count });
			}
		}

		public int getRed() {
			return red;
		}

		public int getGreen() {
			return green;
		}

		public int getBlue() {
			return blue;
		}

	}

	public class HistogramBlock implements Serializable {

		/**
		 *
		 */
		private static final long serialVersionUID = -8536613868524949100L;

		private int cloorDivide;
		private transient int pic;
		private String comment;
		private Map<String, CloorBlock> map = new HashMap<String, CloorBlock>();

		private int colarBase = (int) Math.pow(2d, 8d);
		private int cloorDiv;

		private transient BufferedImage img;
		private transient ColorModel colorModel;

		private int[] rect;

		private CloorBlock topCloor;

		public HistogramBlock(BufferedImage img, int cloorDivide, int[] rect) {

			this.cloorDivide = cloorDivide;
			cloorDiv = colarBase / cloorDivide;

			this.img = img;
			colorModel = ColorModel.getRGBdefault();
			this.rect = rect;

		}

		public void regist() {

			int[][][] rgbArray = new int[this.cloorDivide][this.cloorDivide][this.cloorDivide];

			// getRGB(int startX, int startY, int w, int h, int[] rgbArray, int
			// offset, int scansize) {
			// int[] rgb = img.getRGB(rect[START_WIDTH], rect[START_HEIGHT],
			// rect[END_WIDTH], rect[END_HEIGHT], new int[rect[END_WIDTH]
			// * rect[END_HEIGHT]], 0, 0);
			//
			// for (int i = 0; i < rgb.length; i++) {
			//
			// int r = calCloorBlock(colorModel.getRed(rgb));
			// int g = calCloorBlock(colorModel.getGreen(rgb));
			// int b = calCloorBlock(colorModel.getBlue(rgb));
			// pic++;
			// rgbArray[r][g][b]++;
			// }

			for (int w = rect[START_WIDTH]; w < rect[START_WIDTH]
					+ rect[BLOCK_WIDTH]; w++) {
				for (int h = rect[START_HEIGHT]; h < rect[START_HEIGHT]
						+ rect[BLOCK_HEIGHT]; h++) {

					int rgb = img.getRGB(w, h);
					int r = calCloorBlock(colorModel.getRed(rgb));
					int g = calCloorBlock(colorModel.getGreen(rgb));
					int b = calCloorBlock(colorModel.getBlue(rgb));
					pic++;
					rgbArray[r][g][b]++;
				}
			}

			for (int r = 0; r < this.cloorDivide; r++) {
				for (int g = 0; g < this.cloorDivide; g++) {
					for (int b = 0; b < this.cloorDivide; b++) {

						if (rgbArray[r][g][b] != 0) {
							CloorBlock cloorBlock = new CloorBlock(r, g, b,
									rgbArray[r][g][b]);
							map.put(r + ":" + g + ":" + b, cloorBlock);
						}

					}
				}
			}

		}

		@Deprecated
		public void regist(int w, int h) {

			// getRGB(int startX, int startY, int w, int h, int[] rgbArray, int
			// offset, int scansize) {
			int rgb = img.getRGB(w, h);
			int r = colorModel.getRed(rgb);
			int g = colorModel.getGreen(rgb);
			int b = colorModel.getBlue(rgb);

			regist(r, g, b);
		}

		@Deprecated
		private void regist(int r, int g, int b) {

			pic++;

			String key = calCloorBlock(r) + ":" + calCloorBlock(g) + ":"
					+ calCloorBlock(b);

			CloorBlock cloorBlock = map.get(key);
			if (cloorBlock == null) {
				cloorBlock = new CloorBlock(calCloorBlock(r), calCloorBlock(g),
						calCloorBlock(b));
				map.put(key, cloorBlock);
			}
			cloorBlock.increment();

		}

		private int calCloorBlock(int cloor) {

			return cloor / cloorDiv;

		}

		private void cal() {

			log.debug(Histogram.this.comment + "\tBLOCK " + comment);

			List<CloorBlock> sort = sortCloorBlock();
			topCloor = sort.get(0);

			for (CloorBlock cloor : sort) {

				cloor.setNum(pic);
				cloor.log();

			}

		}

		public List<CloorBlock> sortCloorBlock() {

			List<CloorBlock> sort = new ArrayList<CloorBlock>();
			sort.addAll(this.map.values());
			Collections.sort(sort, new CloorComparator());

			return sort;

		}

		public CloorBlock getCloorBlock(int depth) {

			List<CloorBlock> sortCloorBlock = sortCloorBlock();

			if (sortCloorBlock.size() > depth) {
				return sortCloorBlock.get(depth);
			} else {
				return null;
			}

		}

		private void freeMemory() {
			this.img = null;
		}

		public Map<String, CloorBlock> getSet() {
			return map;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public CloorBlock getTopCloor() {
			return topCloor;
		}

	}

	public HistogramBlock[][] getHistogramBlockArray() {
		return histogramBlockArray;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
