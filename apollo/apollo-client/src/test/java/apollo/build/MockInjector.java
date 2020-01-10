/*
 *    Copyright 2019-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package apollo.build;

import apollo.internals.DefaultInjector;
import apollo.internals.Injector;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import java.util.Map;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class MockInjector implements Injector {
  private static Map<Class, Object> classMap = Maps.newHashMap();
  private static Table<Class, String, Object> classTable = HashBasedTable.create();
  private static Injector delegate = new DefaultInjector();

  @Override
  public <T> T getInstance(Class<T> clazz) {
    T o = (T) classMap.get(clazz);
    if (o != null) {
      return o;
    }

    if (delegate != null) {
      return delegate.getInstance(clazz);
    }

    return null;
  }

  @Override
  public <T> T getInstance(Class<T> clazz, String name) {
    T o = (T) classTable.get(clazz, name);
    if (o != null) {
      return o;
    }

    if (delegate != null) {
      return delegate.getInstance(clazz, name);
    }

    return null;
  }

  public static void setInstance(Class clazz, Object o) {
    classMap.put(clazz, o);
  }

  public static void setInstance(Class clazz, String name, Object o) {
    classTable.put(clazz, name, o);
  }

  public static void setDelegate(Injector delegateInjector) {
    delegate = delegateInjector;
  }

  public static void reset() {
    classMap.clear();
    classTable.clear();
    delegate = null;
  }
}
