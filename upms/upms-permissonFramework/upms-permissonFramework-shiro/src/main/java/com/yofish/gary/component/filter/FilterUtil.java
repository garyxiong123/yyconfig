/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */
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
package com.yofish.gary.component.filter;


import com.yofish.gary.utils.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Filter工具类
 *
 * @author lishirong
 * @date 2018/1/12
 */
public class FilterUtil {
    /**
     * 将object以json的形式回写到客户端
     *
     * @param response
     * @param object 需要回写的都想
     * @throws Exception
     */
    public static void flushMsgStrToClient(ServletResponse response, Object object)
            throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(JsonUtil.toJson(object));
        response.getWriter().flush();
    }
}
