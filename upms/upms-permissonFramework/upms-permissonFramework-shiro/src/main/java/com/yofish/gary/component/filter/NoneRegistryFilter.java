/*
 *   阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 *   Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
 */
package com.yofish.gary.component.filter;

import com.yofish.gary.api.enums.UpmsResultCode;
import com.youyu.common.api.Result;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import static com.yofish.gary.component.filter.FilterUtil.flushMsgStrToClient;


/**
 *
 * @author 熊成威
 * @date 2018/4/3   
 * @version 1.0
 */
public class NoneRegistryFilter extends FormAuthenticationFilter {

	private static final Logger log = LoggerFactory.getLogger(NoneRegistryFilter.class);

	@Override
	protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
		try {
			flushMsgStrToClient(response,  Result.ok(UpmsResultCode.USER_SESSION_EXPIRED));
		} catch (ServletException e) {
			e.printStackTrace();
		}

	}

}
