package framework.foundation.internals;


import framework.foundation.internals.provider.NullProvider;
import framework.foundation.spi.ProviderManager;
import framework.foundation.spi.provider.Provider;

public class NullProviderManager implements ProviderManager {
  public static final NullProvider provider = new NullProvider();

  @Override
  public String getProperty(String name, String defaultValue) {
    return defaultValue;
  }

  @Override
  public Provider provider(Class clazz) {
    return null;
  }

  @Override
  public String toString() {
    return provider.toString();
  }
}
