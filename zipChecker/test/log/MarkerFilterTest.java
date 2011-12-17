package log;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.file.FileUtilExt;

public class MarkerFilterTest extends TestCase {

	private static Logger log = LoggerFactory.getLogger(FileUtilExt.class);

	public void testDecideObject() {
		log.info("ÉmÅ[É}Éã");
		log.info(Log.OP, "OP");
	}

}
