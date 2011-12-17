package module;

import socre.calculator.ScoreCalculator;
import socre.calculator.ScoreCalculatorByDirFile;
import socre.name.FileNameParseCoreOnly;
import socre.name.FileNameParseImpl;
import socre.name.FileNameParser;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class DefaultModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(ScoreCalculator.class).to(ScoreCalculatorByDirFile.class);

		bind(FileNameParser.class).annotatedWith(Names.named("normal")).to(
				FileNameParseImpl.class);
		bind(FileNameParser.class).annotatedWith(Names.named("core")).to(
				FileNameParseCoreOnly.class);
	}
}
