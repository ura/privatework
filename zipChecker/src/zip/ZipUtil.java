package zip;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import book.BookFileUtil;


public class ZipUtil {

	private static Logger log = LoggerFactory.getLogger(BookFileUtil.class);

	/**
	 * ある程度OKだったらスルーする。 Zipの中に複数ZIPが入っていても、1つ解凍できれば、後ものは
	 */
	private static final int PASS_COUNT = 4;

	public State handle(File is, ZipUtilHandler handler) {

		String ext = FilenameUtils.getExtension(is.getName());
		if (ext.equals("zip")) {
			try {
				_handle(is, handler);
				return State.OK;
			} catch (ZipCheckException e) {

				log.info("ZipCheckException " + is.getPath(), e);
				return e.getState();
			}
		} else {
			return State.NON_ZIP;
		}

	}

	private void _handle(File is, ZipUtilHandler handler) {
		org.apache.tools.zip.ZipFile zip = null;
		try {
			// TODO 不正なファイルを例、１ファイルしか入っていない　等を除外検討

			zip = new org.apache.tools.zip.ZipFile(is);
			handler.start(this, zip);

			Enumeration<ZipEntry> e = zip.getEntries();

			while (e.hasMoreElements()) {
				ZipEntry ze = e.nextElement();

				// System.out.println(ze.getName());

				if (ze.isDirectory()) {
					continue;
				}
				if (ze.getName().endsWith(".zip")) {
					if (handler.needCheck(this, zip, ze)) {

						handler.down(zip, ze);

						File temp = createFile(zip, ze, "zip");
						try {
							_handle(temp, handler);
						} finally {
							temp.delete();
							handler.up(zip, ze);
						}

					}

				} else {

					if (handler.needCheck(this, zip, ze)) {

						handler.hanlde(this, zip, ze);

					}

				}
			}

			handler.end(this, zip);

		} catch (ZipException e) {
			throw new ZipCheckException("ZIP OPEN ERROR", State.ZIP_OPEN_ERROR,
					e);
		} catch (IOException e) {
			throw new ZipCheckException("ZIP OPEN ERROR", State.ZIP_OPEN_ERROR,
					e);
		} finally {
			close(zip);
		}

	}

	private void close(ZipFile c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {

			}
		}
	}

	private void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {

			}
		}
	}

	/**
	 *
	 * @param zis
	 */
	public void testRead(ZipFile zip, ZipEntry ze) {
		// インターフェース上、アウトプットストリームを渡すことになっているが、
		// 読めるか確かめるだけなので、結果はガンガン捨ててかまわない
		NullOutputStream baos = new NullOutputStream();
		readZip(zip, ze, baos);

	}

	public OutputStream readZip(ZipFile zip, ZipEntry ze, OutputStream bos) {
		InputStream zis = null;

		try {

			try {
				zis = new BufferedInputStream(zip.getInputStream(ze));
			} catch (ZipException e1) {
				throw new ZipCheckException("ZIP OPEN ERROR",
						State.ZIP_OPEN_ERROR, e1);
			} catch (IOException e1) {
				throw new ZipCheckException("ZIP OPEN ERROR",
						State.ZIP_OPEN_ERROR, e1);
			}

			try {
				final int BUF_SIZE = 1024 * 8;

				byte[] buf = new byte[BUF_SIZE];
				for (;;) {
					int len = zis.read(buf);

					if (len < 0) {
						break;
					}
					bos.write(buf, 0, len);
				}
			} catch (IOException e) {

				throw new ZipCheckException("パスワードによって読み取れない・・？",
						State.UNZIP_ERROR, e);
			}

		} finally {
			close(zis);
		}

		return bos;
	}

	public File createFile(ZipFile zip, ZipEntry ze, String ext) {
		BufferedOutputStream bos = null;
		try {

			File f = File.createTempFile("ZIPUTIL_", ext);
			File f2 = new File(f.getAbsoluteFile() + "." + ext);
			bos = new BufferedOutputStream(new FileOutputStream(f2));

			readZip(zip, ze, bos);
			return f2;
		} catch (IOException e) {

			throw new ZipCheckException("その他のエラー", State.OTHER, e);
		} finally {
			close(bos);
		}
	}
}
