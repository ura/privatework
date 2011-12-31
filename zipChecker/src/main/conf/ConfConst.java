package conf;

public class ConfConst {

	public static final String ARC_WORK_DIR = "ARC_WORK_DIR";
	public static final String WINRAR_PATH = "WINRAR_PATH";
	public static final String SmillaEnlargerCL_PATH = "SmillaEnlargerCL_PATH";

	public static final String AWS_ACCESS_KEY_ID = "AWS_ACCESS_KEY_ID";
	public static final String AWS_SECRET_KEY = "AWS_SECRET_KEY";
	public static final String RAKUTEN_KEY = "RAKUTEN_KEY";

	public static final String THREAD_GET_BOOKINFO = "THREAD_GET_BOOKINFO";
	public static final String THREAD_DECODE = "THREAD_DECODE";

	public static final String THREAD_BARCODE = "THREAD_BARCODE";
	public static final String THREAD_BARCODE_DERAY = "THREAD_BARCODE_DERAY";

	public static final String MAIN_CONF_FILE = "/main.properties";

	public static Config MAIN_CONF = Config.loadConfig(MAIN_CONF_FILE,
			Config.class);

}
