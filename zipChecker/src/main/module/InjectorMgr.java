package module;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class InjectorMgr {
	public static Injector get() {
		return Guice.createInjector(new DefaultModule());

	}

	public static Injector get(Module module) {
		return Guice.createInjector(module);

	}
}
