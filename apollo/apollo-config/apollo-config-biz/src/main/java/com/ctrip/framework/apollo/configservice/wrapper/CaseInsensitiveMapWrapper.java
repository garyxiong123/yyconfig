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
package com.ctrip.framework.apollo.configservice.wrapper;

import java.util.Map;

/**
 *  大小写 不敏感的Map
 * @param <T>
 */
public class CaseInsensitiveMapWrapper<T> {
  private final Map<String, T> delegate;

  public CaseInsensitiveMapWrapper(Map<String, T> delegate) {
    this.delegate = delegate;
  }

  public T get(String key) {
    return delegate.get(key.toLowerCase());
  }

  public T put(String key, T value) {
    return delegate.put(key.toLowerCase(), value);
  }

  public T remove(String key) {
    return delegate.remove(key.toLowerCase());
  }
}
