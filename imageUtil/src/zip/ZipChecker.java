package zip;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ZIPの状態判別用。
 * 暗号化されているZIPや多重ZIP、ZIP破損等を判定する。
 * @author name
 *
 */
public class ZipChecker {

	private static Logger log = LoggerFactory.getLogger(ZipChecker.class);

	/**
	 * ある程度OKだったらスルーする。 Zipの中に複数ZIPが入っていても、1つ解凍できれば、後ものは
	 */
	private static final int PASS_COUNT = 4;

	public static State check(File is) {

		return new ZipUtil().handle(is, new ZipCheckHandler());

	}

}
