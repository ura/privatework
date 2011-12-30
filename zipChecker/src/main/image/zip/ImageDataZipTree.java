package image.zip;

import image.ImageDataTree;
import image.Img;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Enumeration;
import java.util.zip.ZipException;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.file.DirCollector;
import util.file.FileWalker;
import zip.State;
import zip.ZipCheckException;

public class ImageDataZipTree extends ImageDataTree {

	private static Logger log = LoggerFactory.getLogger(ImageDataZipTree.class);
	private static final int PASS_COUNT = 4;

	public ImageDataZipTree(int depth, String key, File path) {
		super(depth, key, path);
	}

	public ImageDataZipTree(int depth, String key, String path) {
		super(depth, key, path);
	}

	public void walk() {

		DirCollector srcDir = new DirCollector();
		new FileWalker().walk(getPath(), srcDir);
		Collection<String> allFileFullPath = srcDir.getAllFilePath();

		int count = 0;

		for (String path : allFileFullPath) {
			if (path.endsWith(".zip")) {

				Img img = new Img(path);
				img.createHistogram(true);
				add(img);
				count++;

				if (count % 10 == 0) {

					log.info("処理中です・・・・  終了枚数: " + count);
				}
			}

		}

	}

	private static void _check(File is) {
		org.apache.tools.zip.ZipFile zip = null;
		try {
			// TODO 不正なファイルを例、１ファイルしか入っていない　等を除外検討

			zip = new org.apache.tools.zip.ZipFile(is);

			Enumeration<ZipEntry> e = zip.getEntries();

			boolean passCheck = false;
			int nozipcount = 0;
			int zipcount = 0;

			while (e.hasMoreElements()) {
				ZipEntry ze = e.nextElement();

				// System.out.println(ze.getName());

				if (ze.isDirectory()) {
					continue;
				}
				if (ze.getName().endsWith(".zip")) {
					if (zipcount < PASS_COUNT) {
						File temp = createFile(zip, ze);
						_check(temp);
						temp.delete();
						zipcount++;
					}

				} else {

					if (!passCheck) {

						testRead(zip, ze);

						if (nozipcount > PASS_COUNT) {
							passCheck = true;
						}
					}
					nozipcount++;

				}
				// System.out.println(ze.getName());
			}

			if (zipcount < 2 && nozipcount < 7) {
				throw new ZipCheckException("不正なファイルの気がします", State.FEW_FILE);
			}

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

	private static void close(ZipFile c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {

			}
		}
	}

	private static void close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (IOException e) {

			}

		}
	}

	private static void testRead(ZipFile zip, ZipEntry ze) {
		// インターフェース上、アウトプットストリームを渡すことになっているが、
		// 読めるか確かめるだけなので、結果はガンガン捨ててかまわない
		NullOutputStream baos = new NullOutputStream();
		readZip(zip, ze, baos);

	}

	private static File createFile(ZipFile zip, ZipEntry ze) {
		BufferedOutputStream bos = null;
		try {

			File f = File.createTempFile("temp", "zip");
			bos = new BufferedOutputStream(new FileOutputStream(f));

			readZip(zip, ze, bos);

			return f;
		} catch (IOException e) {

			throw new ZipCheckException("その他のエラー", State.OTHER, e);
		} finally {
			close(bos);
		}
	}

	private static OutputStream readZip(ZipFile zip, ZipEntry ze,
			OutputStream bos) {
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

}
