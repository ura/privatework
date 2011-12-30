package main;

import java.io.File;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import book.BookFileUtil;
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
			BookFileUtil.rebuildArcWithUI(args[1], args[2], args[3]);
		}

	}

	private static void checkZip(String args[]) {

		if (args.length == 1) {
			BookFileUtil.movePassZipAll(".");
		} else {
			BookFileUtil.movePassZipAll(args[1]);
		}
	}

	private static void createDir(String args[]) {

		if (args.length == 1) {
			BookFileUtil.createDir(".");
		} else {
			BookFileUtil.createDir(args[1]);
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

			BookFileUtil.classifyAll(strings[0], new String[] { strings[2] },
					strings[1]);

			//FileUtil.deletePassZipAll(strings[1]);

			BookFileUtil.deleteSameFile(new File(strings[1]));
		}
	}

}
