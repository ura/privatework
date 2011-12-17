package zip;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.file.FileUtilExt;

public class ZipChecker {

	private static Logger log = LoggerFactory.getLogger(FileUtilExt.class);

	/**
	 * ������xOK��������X���[����B Zip�̒��ɕ���ZIP�������Ă��Ă��A1�𓀂ł���΁A����̂�
	 */
	private static final int PASS_COUNT = 4;

	public static State check(File is) {

		return new ZipUtil().handle(is, new ZipCheckHandler());

	}

}
