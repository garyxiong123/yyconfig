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

import com.google.common.base.Splitter;

import javax.servlet.http.HttpServletRequest;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/13 下午11:02
 */
public class YyHttpUtils {
    public static final Splitter X_FORWARDED_FOR_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();


    public static String tryToGetClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-FORWARDED-FOR");
        if (!isNullOrEmpty(forwardedFor)) {
            return X_FORWARDED_FOR_SPLITTER.splitToList(forwardedFor).get(0);
        }
        return request.getRemoteAddr();
    }
}
