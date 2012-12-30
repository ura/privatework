package servlet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.apache.commons.lang3.StringUtils;

import book.BarcodeReader4Book;
import book.BarcodeReader4Book.Task;

/**
 *
 * １ファイルを受け取り、ISBNを返す。
 *
 */
@WebServlet(name = "FileUpload", urlPatterns = { "/FileUpload" })
@MultipartConfig(fileSizeThreshold = 5000000, maxFileSize = 20000000, location = "C:/Temp")
public class FileUploadServlet extends HttpServlet {

	private static final String path = "C:/Temp";;

	@Override
	public void init() throws ServletException {

		super.init();

		new File(path).mkdir();
		System.out.println(new File(path).getAbsolutePath());
		System.out.println(new File(path).exists());

		try {
			Thread.sleep(2000l);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// <INPUT type=”file” name=”content”> で指定した名前より取得
		Collection<Part> parts = request.getParts();

		PrintWriter out = response.getWriter();

		// ファイル名の取得
		for (Part part : parts) {
			try {
				String name = getFilename(part);

				// ファイルの保存 @MultipartConfig(location=”/tmp”) で設定した
				// ディレクトリ配下に保存される。
				File temp = new File(path + File.separator + name);
				temp.delete();
				part.write(name);

				File dest = File.createTempFile("IMG_", "");
				dest.delete();
				System.out.println(dest.getAbsolutePath() + "\t\t"
						+ dest.exists());

				Files.move(temp.toPath(), dest.toPath());

				BarcodeReader4Book barcodeReader4Book = new BarcodeReader4Book();
				//INDEXはダミー
				Task<String> task = barcodeReader4Book.new Task<String>(0,
						false, dest);

				String barcode = task.call();
				if (StringUtils.isEmpty(barcode)) {
					barcode = "null";
				}
				out.println(barcode);
				System.out.println(barcode);

				temp.delete();
				dest.delete();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		out.flush();
		out.close();
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		processRequest(request, response);
	}

	private String getFilename(Part part) {
		for (String cd : part.getHeader("Content-Disposition").split(";")) {

			System.out.println(cd);
			if (cd.trim().startsWith("filename")) {
				return cd.substring(cd.indexOf("=") + 1).trim()
						.replace("\"", "");
			}
		}
		return null;
	}
}
