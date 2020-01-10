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
package framework.apollo.core.enums;


import common.utils.YyStringUtils;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public enum ConfigFileFormat {
  Properties("properties"), XML("xml"), JSON("json"), YML("yml"), YAML("yaml");

  private String value;

  ConfigFileFormat(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static ConfigFileFormat fromString(String value) {
    if (YyStringUtils.isEmpty(value)) {
      throw new IllegalArgumentException("value can not be empty");
    }
    switch (value) {
      case "properties":
        return Properties;
      case "xml":
        return XML;
      case "json":
        return JSON;
      case "yml":
        return YML;
      case "yaml":
        return YAML;
    }
    throw new IllegalArgumentException(value + " can not map enum");
  }

  public static boolean isValidFormat(String value) {
    try {
      fromString(value);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
