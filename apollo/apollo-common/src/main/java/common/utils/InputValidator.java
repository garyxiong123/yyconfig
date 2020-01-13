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
package common.utils;


import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
public class InputValidator {
    public static final String INVALID_CLUSTER_NAMESPACE_MESSAGE = "只允许输入数字，字母和符号 - _ .";
    public static final String INVALID_NAMESPACE_NAMESPACE_MESSAGE = "不允许以.json, .yml, .yaml, .xml, .properties结尾";
    public static final String CLUSTER_NAMESPACE_VALIDATOR = "[0-9a-zA-Z_.-]+";
    public static final String APP_NAMESPACE_VALIDATOR = "[a-zA-Z0-9._-]+(?<!\\.(json|yml|yaml|xml|properties))$";
    private static final Pattern CLUSTER_NAMESPACE_PATTERN = Pattern.compile(CLUSTER_NAMESPACE_VALIDATOR);
    private static final Pattern APP_NAMESPACE_PATTERN = Pattern.compile(APP_NAMESPACE_VALIDATOR);


    public static boolean isValidClusterNamespace(String input) {
        Matcher matcher = CLUSTER_NAMESPACE_PATTERN.matcher(input);
        return matcher.matches();
    }

    public static boolean isValidAppNamespace(String name) {
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        return CLUSTER_NAMESPACE_PATTERN.matcher(name).matches() && APP_NAMESPACE_PATTERN.matcher(name).matches();
    }
}
