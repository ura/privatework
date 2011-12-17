package conf;

public class ConfConst {

	public static final String ARC_WORK_DIR = "ARC_WORK_DIR";
	public static final String WINRAR_PATH = "WINRAR_PATH";
	public static final String MAIN_CONF_FILE = "/main.properties";

	public static Config MAIN_CONF = Config.loadConfig(MAIN_CONF_FILE,
			Config.class);

}
