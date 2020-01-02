/*
 * 阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 * Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
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
