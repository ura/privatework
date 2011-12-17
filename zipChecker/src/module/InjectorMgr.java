package module;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class InjectorMgr {
	public static Injector get() {
		return Guice.createInjector(new DefaultModule());

	}
}
