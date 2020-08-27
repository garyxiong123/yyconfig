package com.ctrip.framework.apollo.configservice.component.util;

import com.google.common.base.Splitter;

import javax.servlet.http.HttpServletRequest;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明
 * @Date: 2020/8/18 下午1:54
 */
public class IpUtils {

    private static final Splitter X_FORWARDED_FOR_SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();


    public static String tryToGetClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-FORWARDED-FOR");
        if (!isNullOrEmpty(forwardedFor)) {
            return X_FORWARDED_FOR_SPLITTER.splitToList(forwardedFor).get(0);
        }
        return request.getRemoteAddr();
    }


}
