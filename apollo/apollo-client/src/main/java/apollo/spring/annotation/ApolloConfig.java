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
package apollo.spring.annotation;


import framework.apollo.core.ConfigConsts;

import java.lang.annotation.*;

/**
 * Use this annotation to inject Apollo Config Instance.
 *
 * <p>Usage example:</p>
 * <pre class="code">
 * //Inject the config for "someNamespace"
 * &#064;ApolloConfig("someNamespace")
 * private Config config;
 * </pre>
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ApolloConfig {
  /**
   * Apollo appNamespace for the config, if not specified then default to application
   */
  String value() default ConfigConsts.NAMESPACE_APPLICATION;
}
