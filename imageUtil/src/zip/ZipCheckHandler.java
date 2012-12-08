package zip;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public class ZipCheckHandler implements ZipUtilHandler {

	private int nozipcount = 0;
	private int zipcount = 0;

	public void hanlde(ZipUtil util, ZipFile zip, ZipEntry ze) {
		util.testRead(zip, ze);
	}

	public boolean needCheck(ZipUtil util, ZipFile zip, ZipEntry ze) {

		boolean result = true;
		// 判定
		if (ze.getName().endsWith(".zip")) {

			//ZIPの中のZIPが回答したことがあれば、OKで
			if (zipcount > 3) {
				result = false;
			}

		} else {
			//10枚程度回答できていれば、OKで
			if (zipcount > 10) {
				result = false;
			}
		}

		// 集計
		if (ze.getName().endsWith(".zip")) {
			zipcount++;
		} else {
			nozipcount++;
		}

		return true;
	}

	@Override
	public void end(ZipUtil util, ZipFile zip) throws ZipCheckException {
		if (zipcount < 2 && nozipcount < 7) {
			throw new ZipCheckException("不正なファイルの気がします", State.FEW_FILE);
		}

	}

	@Override
	public void down(ZipFile zip, ZipEntry ze) {

	}

	@Override
	public void up(ZipFile zip, ZipEntry ze) {

	}

	@Override
	public void start(ZipUtil util, ZipFile zip) throws ZipCheckException {

	}
}
