package book;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import conf.ConfConst;

public class BookImgUploader {

	private static Logger log = LoggerFactory.getLogger(BookImgUploader.class);

	public String getBarcpdeInfoFromServer(File f)
			throws FileNotFoundException, IOException, HttpException {

		//CONFIGから一つ値をとる。
		String urlRandam = ConfConst.MAIN_CONF
				.getRandamVal(ConfConst.BARCODE_REMOTE_SITES);

		HttpClient client = new org.apache.commons.httpclient.HttpClient();
		PostMethod postMethod = new PostMethod(urlRandam);

		//TODO 設定値決め打ちでいいと考えるが？
		client.setConnectionTimeout(60 * 5 * 1000);

		log.debug("File Length = {}", f.length());

		FilePart[] parts = new FilePart[] { new FilePart(f.getName(), f) };
		postMethod.setRequestEntity(new MultipartRequestEntity(parts,
				postMethod.getParams()));

		int statusCode1 = client.executeMethod(postMethod);

		log.debug("statusLine>>> {}", postMethod.getStatusLine());

		String barcode = postMethod.getResponseBodyAsString(100).trim();
		if (barcode.equals("null")) {
			barcode = null;
		}
		log.info("[{}]", barcode);

		postMethod.releaseConnection();

		return barcode;
	}
}
