/*
 *   阔地教育科技有限公司版权所有（codyy.com/codyy.cn）
 *   Copyright (c) 2017, Codyy and/or its affiliates. All rights reserved.
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

import com.yofish.gary.api.enums.UpmsResultCode;
import com.youyu.common.api.Result;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

import static com.yofish.gary.component.filter.FilterUtil.flushMsgStrToClient;


/**
 *
 * @author 熊成威
 * @date 2018/7/23   
 * @version 1.0
 */
public class NoneLoginFilter extends BasicHttpAuthenticationFilter {

	@Override
	protected void redirectToLogin(ServletRequest request, ServletResponse response) throws IOException {
		try {
			flushMsgStrToClient(response,  Result.ok(UpmsResultCode.USER_SESSION_EXPIRED));
		} catch (ServletException e) {
			e.printStackTrace();
		}

	}
}
