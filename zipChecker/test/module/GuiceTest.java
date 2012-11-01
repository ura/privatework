package module;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.perf4j.LoggingStopWatch;
import org.perf4j.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.matcher.Matchers;


public class GuiceTest {
	private static Logger log = LoggerFactory
			.getLogger("org.perf4j.TimingLogger");

	public static void main(String[] args) {

		// コンテナ
		Service s = Guice.createInjector(new AbstractModule() {

			protected void configure() {
				bind(IOya.class).to(Ko1.class);
				binder().bind(Service.class);
				// メソッド名を出力するインターセプタを適用
				binder().bindInterceptor(Matchers.any(), Matchers.any(),
						new MethodInterceptor() {
							@Override
							public Object invoke(MethodInvocation mi)
									throws Throwable {
								//								System.out.println("☆intercept:"
								//										+ mi.getThis().getClass().getName()
								//												.split("\\$")[0] + "."
								//										+ mi.getMethod().getName());

								StopWatch stopWatch = new LoggingStopWatch(mi
										.getThis().getClass().getName()
										.split("\\$")[0]
										+ "." + mi.getMethod().getName());

								Object x = mi.proceed();

								//								System.out.println("★intercept:"
								//										+ mi.getThis().getClass().getName()
								//												.split("\\$")[0] + "."
								//										+ mi.getMethod().getName());
								log.info(stopWatch.stop());
								return x;
							}
						});

			}

		}).getInstance(Service.class);
		s.all();
	}
}
