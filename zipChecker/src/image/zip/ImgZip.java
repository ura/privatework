package image.zip;

import image.Img;

import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImgZip extends Img {

	private static Logger log = LoggerFactory.getLogger(ImgZip.class);

	public ImgZip(String filename,String entrys[]) {
		super(filename);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public void loadImage() {
		try {
			image = ImageIO.read(imgFile);
			this.w = image.getWidth();
			this.h = image.getHeight();

		} catch (IOException e) {
			log.error("イメージロードエラー {}", imgFile.getName(), e);

		}
	}
}
