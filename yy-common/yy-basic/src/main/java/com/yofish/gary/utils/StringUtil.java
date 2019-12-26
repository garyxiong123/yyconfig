/*
 *    Copyright 2018-2019 the original author or authors.
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
package com.yofish.gary.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年5月30日 10:00:00
 * @work 字符串工具类
 */
public class StringUtil extends StringUtils {

    /**
     * 参考StringUtils.equals(cs1,cs2)
     *
     * @param cs1
     * @param cs2
     * @return
     */
    public static boolean eq(final CharSequence cs1, final CharSequence cs2) {
        return equals(cs1, cs2);
    }

    /**
     * 要求parameter不为Blank,并提供描述信息
     *
     * @param parameter
     * @param message
     */
    public static void checkNonBlank(String parameter, String message) {
        if (isBlank(parameter)) {
            throw new RuntimeException(message);
        }
    }
}
