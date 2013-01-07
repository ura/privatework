package main;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import book.BookFileUtil;

public class RebuildCUI {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Option exclude = OptionBuilder.withLongOpt("exclude")
				.withValueSeparator(';').withDescription("除外対象").hasArgs().

				withArgName("exclude").create("e");

		Option keyword = OptionBuilder.withLongOpt("keyword")
				.withDescription("対象とするキーワド").isRequired().hasArgs()
				.withValueSeparator(';').withArgName("keyword").create('k');

		Option input = OptionBuilder.withLongOpt("input")
				.withDescription("対象ディレクトリ").isRequired().hasArgs()
				.withValueSeparator(';').withArgName("input").create('i');

		Options opts = new Options();
		opts.addOption(input).addOption(keyword).addOption(exclude);

		//usageを表示してみる
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("オプションで全て指定", opts);

		CommandLineParser parser = new BasicParser();
		CommandLine cl = null;
		try {
			System.out.println(args.length);
			cl = parser.parse(opts, args);

		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}

		List<String> excludeList = Arrays.asList(cl.getOptionValues("exclude"));
		List<String> keywordList = Arrays.asList(cl.getOptionValues("keyword"));
		List<String> inputList = Arrays.asList(cl.getOptionValues("input"));

		for (String i : inputList) {
			BookFileUtil.rebuildArcCLI(i, keywordList, excludeList);

		}

		System.exit(0);

	}
}
