package module;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import socre.calculator.ScoreCalculator;
import socre.calculator.ScoreCalculatorByDirFile;
import socre.name.FileNameParseCoreOnly;
import socre.name.FileNameParseImpl;
import socre.name.FileNameParser;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

public class DefaultModule extends AbstractModule {
	private static Logger log = LoggerFactory
			.getLogger("org.perf4j.TimingLogger");

	@Override
	protected void configure() {
		bind(ScoreCalculator.class).to(ScoreCalculatorByDirFile.class);

		bind(FileNameParser.class).annotatedWith(Names.named("normal")).to(
				FileNameParseImpl.class);
		bind(FileNameParser.class).annotatedWith(Names.named("core")).to(
				FileNameParseCoreOnly.class);

		//	bind(BarcodeReader4Book.class).to(BarcodeReader4Book.class);
		//	bind(BookNameUtil.class).to(BookNameUtil.class);

		//bindInterceptor(any(), any(), new LoggingInterceptor());
		//interceptorStopWatch();
	}

	protected void interceptorStopWatch() {
		binder().bindInterceptor(Matchers.any(), Matchers.any(),
				new MethodInterceptor() {
					@Override
					public Object invoke(MethodInvocation mi) throws Throwable {

						String t = mi.getThis().getClass().getName()
								.split("\\$")[0]
								+ "." + mi.getMethod().getName();
						StopWatch stopWatch = new StopWatch();

						Object x = mi.proceed();

						String stop = stopWatch.stop(t);
						//long elapsedTime = stopWatch.getElapsedTime();

						log.info(stop);
						return x;
					}
				});
	}

}
