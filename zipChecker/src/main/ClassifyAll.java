package main;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.file.FileUtilExt;
import conf.Config;
import conf.ListConfig;

/**
 * MAINクラス。分類したり、フォルダを定義したり。

 *
 */
public class ClassifyAll {

	private static Logger log = LoggerFactory.getLogger(ClassifyAll.class);

	public static void main(String[] args) {

		String cmd = args[0];
		if (cmd.equals("classfy")) {
			classfy(args);
		} else if (cmd.equals("createDir")) {
			createDir(args);
		} else if (cmd.equals("checkZip")) {
			checkZip(args);
		} else if (cmd.equals("rebuildArc")) {
			FileUtilExt.rebuildArcWithUI(args[1], args[2], args[3]);
		}

	}

	private static void checkZip(String args[]) {

		if (args.length == 1) {
			FileUtilExt.movePassZipAll(".");
		} else {
			FileUtilExt.movePassZipAll(args[1]);
		}
	}

	private static void createDir(String args[]) {

		if (args.length == 1) {
			FileUtilExt.createDir(".");
		} else {
			FileUtilExt.createDir(args[1]);
		}
	}

	private static void classfy(String args[]) {
		ListConfig config;
		if (args.length == 1) {
			config = Config.loadConfig("/prop.properties", ListConfig.class);
		} else {
			config = Config.loadConfig(args[1], ListConfig.class);
		}

		Collection<String[]> setting = config.getMoveSetting();

		for (String[] strings : setting) {
			log.info("{},{},{}", new Object[] { strings[0], strings[2],
					strings[1] });

			FileUtilExt.classifyAll(strings[0], new String[] { strings[2] },
					strings[1]);

			//FileUtil.deletePassZipAll(strings[1]);

			FileUtilExt.deleteSameFile(strings[1]);
		}
	}

}
