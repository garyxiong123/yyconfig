package framework.foundation.spi;


import framework.foundation.spi.provider.Provider;

public interface ProviderManager {
  public String getProperty(String name, String defaultValue);

  public <T extends Provider> T provider(Class<T> clazz);
}
