package zip;

import image.Img;
import image.ImgUtil;

import java.io.File;
import java.util.Collection;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZipGetHandler implements ZipUtilHandler {

	private static Logger log = LoggerFactory.getLogger(ZipGetHandler.class);

	public String entrys[];
	public int depth = 0;

	public ZipGetHandler(String[] entry) {
		this.entrys = entry;
	}

	public ZipGetHandler(Collection<String> entry) {
		this.entrys = entry.toArray(new String[0]);
	}

	public void hanlde(ZipUtil util, ZipFile zip, ZipEntry ze) {
		File createFile = util.createFile(zip, ze, "jpg");
		log.debug(createFile.getAbsolutePath());
		Img img = new Img(createFile);

		ImgUtil.view(img);

	}

	public boolean needCheck(ZipUtil util, ZipFile zip, ZipEntry ze) {

		for (String e : entrys) {
			String[] zipPath = e.split(":");

			if (depth < zipPath.length && ze.getName().equals(zipPath[depth])) {
				return true;
			}
		}

		return false;

	}

	@Override
	public void end(ZipUtil util, ZipFile zip) throws ZipCheckException {

	}

	@Override
	public void down(ZipFile zip, ZipEntry ze) {
		depth++;
	}

	@Override
	public void up(ZipFile zip, ZipEntry ze) {
		depth--;
	}

	@Override
	public void start(ZipUtil util, ZipFile zip) throws ZipCheckException {
		// TODO 自動生成されたメソッド・スタブ

	}
}
