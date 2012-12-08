package image;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Img implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1664505888813113277L;

	private static Logger log = LoggerFactory.getLogger(Img.class);

	protected File imgFile;
	protected transient BufferedImage image;

	private Histogram histogram;
	protected int w;
	protected int h;
	protected boolean color;

	private int numComponents;

	public Img(File file) {

		imgFile = file;
		loadImage();
	}

	public Img(String filename) {

		imgFile = new File(filename);
		loadImage();
	}

	/**
	 * イメージをロードします。
	 */
	public void loadImage() {
		try {
			image = ImageIO.read(imgFile);
			this.w = image.getWidth();
			this.h = image.getHeight();
			numComponents = this.image.getColorModel().getColorSpace()
					.getNumComponents();
			if (numComponents > 1) {
				color = true;
			} else {
				color = false;
			}

		} catch (IOException e) {
			log.error("イメージロードエラー {}", imgFile.getName(), e);

		}
	}

	public boolean isColor() {

		return this.color;

	}

	/**
	 * サイズを指定してロードします
	 *
	 * @param w
	 * @param h
	 * @return
	 */
	protected BufferedImage loadImage(int w, int h) {

		if (this.image == null) {
			loadImage();
		}

		BufferedImage shrinkImage = new BufferedImage(w, h, image.getType());
		AffineTransformOp atOp = new AffineTransformOp(
				AffineTransform.getScaleInstance(
						((double) w) / image.getWidth(),
						((double) h) / image.getHeight()), null);
		atOp.filter(image, shrinkImage);

		freeMemory();

		return shrinkImage;
	}

	/**
	 * 色ヒストグラムを生成します。
	 *
	 * @param frreImage
	 */
	public void createHistogram(boolean frreImage) {

		this.histogram = createHistogram(frreImage, Histogram.Settings.LOW);
	}

	public Histogram createHistogram(Histogram.Settings setting) {
		return createHistogram(true, setting);
	}

	/**
	 * 色ヒストグラムを生成します。
	 *
	 * @param frreImage
	 */
	private Histogram createHistogram(boolean frreImage,
			Histogram.Settings setting) {

		if (this.image == null) {
			loadImage();
		}

		Histogram tempHistogram = new Histogram(setting);
		tempHistogram.setComment(imgFile.getName() + "  "
				+ this.image.getWidth() + ":" + this.image.getHeight());
		tempHistogram.createHistogram(this.image);

		if (frreImage) {
			freeMemory();
			tempHistogram.freeMemory();
		}

		return tempHistogram;
	}

	public void createHistogram() {

		createHistogram(false);
	}

	public void freeMemory() {
		this.image = null;
	}

	private void initFrame(String imageName, Image image) {
		JFrame frame = new JFrame(imageName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		ImageIcon icon = new ImageIcon(image);
		JLabel label = new JLabel(icon);

		frame.getContentPane().add(label);
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		if (args.length == 1) {
			Img img = new Img(args[0]);
			img.createHistogram();
			img.initFrame(args[0], img.image);

		} else {
			System.out.println("Usage: java ImageIOTest1 ");
		}
	}

	public Histogram getHistogram() {
		return histogram;
	}

	public String toString() {
		return imgFile.getName();
	}

	public String getInfo() {

		StringBuilder sb = new StringBuilder();

		sb.append("path:").append(imgFile.getAbsolutePath()).append("  ");
		sb.append("size:").append(w).append("*").append(h).append(" (")
				.append(this.imgFile.length() / 1000).append("kb)").append(" ");
		sb.append("color:").append(color).append(":").append(numComponents);

		return sb.toString();
	}

	public BufferedImage getImage() {
		return image;
	}

	public File getImgFile() {
		return imgFile;
	}

	public boolean equalImage(Img o1) {

		if (this.imgFile.equals(o1.image)) {
			return true;
		}

		if (this.w == o1.w && this.h == o1.h
				&& this.imgFile.length() == o1.imgFile.length()
				&& this.imgFile.getName() == o1.imgFile.getName()
		// && this.imgFile.lastModified() == o1.imgFile.lastModified()

		) {
			return true;
		} else {
			return false;
		}

	}

}
