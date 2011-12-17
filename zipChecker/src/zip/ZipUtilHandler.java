package zip;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

public interface ZipUtilHandler {

	public void hanlde(ZipUtil util,ZipFile zip,ZipEntry ze) ;

	public boolean needCheck(ZipUtil util,ZipFile zip,ZipEntry ze) ;

	public void start(ZipUtil util,ZipFile zip) throws ZipCheckException;
	public void end(ZipUtil util,ZipFile zip) throws ZipCheckException;

	public void up(ZipFile zip,ZipEntry ze);
	public void down(ZipFile zip,ZipEntry ze);

}
