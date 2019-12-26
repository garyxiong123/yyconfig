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
package com.yofish.gary.biz.helper.header;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

import static java.util.Collections.*;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月25日 10:00:00
 * @work ops header helper
 */
public class YyServletRequestHelper extends HttpServletRequestWrapper {

    private Map<String, String> opsHeader = new HashMap<>();

    public YyServletRequestHelper(HttpServletRequest request) {
        super(request);
    }

    public YyServletRequestHelper(HttpServletRequest request, Map<String, String> opsHeader) {
        this(request);
        this.opsHeader = opsHeader;
    }

    @Override
    public String getHeader(String name) {
        return opsHeader.getOrDefault(name, super.getHeader(name));
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        Enumeration<String> headers = super.getHeaders(name);
        if (headers == null || !headers.hasMoreElements()) {
            String header = opsHeader.get(name);
            if (hasText(header)) {
                headers = enumeration(singletonList(header));
            }
        }
        return headers;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Enumeration<String> headerNames = super.getHeaderNames();
        ArrayList<String> list = list(headerNames);
        Set<String> set = new HashSet<>(list);
        if (!isEmpty(opsHeader.keySet())) {
            set.addAll(opsHeader.keySet());
        }
        return enumeration(set);
    }
}
