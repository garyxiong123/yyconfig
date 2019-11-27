package apollo.spring.util;

import apollo.exceptions.ApolloConfigException;
import apollo.spring.config.ConfigPropertySourceFactory;
import apollo.spring.property.PlaceholderHelper;
import apollo.spring.property.SpringValueRegistry;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import framework.apollo.tracer.Tracer;

public class SpringInjector {
  private static volatile Injector s_injector;
  private static final Object lock = new Object();

  private static Injector getInjector() {
    if (s_injector == null) {
      synchronized (lock) {
        if (s_injector == null) {
          try {
            s_injector = Guice.createInjector(new SpringModule());
          } catch (Throwable ex) {
            ApolloConfigException exception = new ApolloConfigException("Unable to initialize Apollo Spring Injector!", ex);
            Tracer.logError(exception);
            throw exception;
          }
        }
      }
    }

    return s_injector;
  }

  public static <T> T getInstance(Class<T> clazz) {
    try {
      return getInjector().getInstance(clazz);
    } catch (Throwable ex) {
      Tracer.logError(ex);
      throw new ApolloConfigException(
          String.format("Unable to load instance for %s!", clazz.getName()), ex);
    }
  }

  private static class SpringModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(PlaceholderHelper.class).in(Singleton.class);
      bind(ConfigPropertySourceFactory.class).in(Singleton.class);
      bind(SpringValueRegistry.class).in(Singleton.class);
    }
  }
}
