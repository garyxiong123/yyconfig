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
package com.yofish.yyconfig.common.common.filter;

import org.apache.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author 熊成威
 * @date 2017/10/26
 * @version 1.0
 */
public class YyCorsFilter implements Filter {
//	@Override
//	public void destroy() {
//
//	}

//	@Override
//	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
//		HttpServletRequest httpServletRequest = (HttpServletRequest) req;
//		HttpServletResponse response = (HttpServletResponse) resp;
//		response.setHeader("Access-Control-Allow-Origin", httpServletRequest.getHeader("Origin"));
//		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//		response.setHeader("Access-Control-Max-Age", "3600");
//		response.setHeader("Access-Control-Allow-Headers",
//				"Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token,content-type");
//		response.setHeader("Access-Control-Allow-Credentials", "true");
//		response.setHeader("Access-Control-Expose-Headers", "X-PINGOTHER, Origin,token, X-Requested-With, Content-Type, Accept,auth_token,Accept-Charset");
//		// 这里通过判断请求的方法，判断此次是否是预检请求，如果是，立即返回一个204状态吗，标示，允许跨域；预检后，正式请求，这个方法参数就是我们设置的post了
//		if ("OPTIONS".equals(httpServletRequest.getMethod())) {
//			response.setStatus(HttpStatus.SC_NO_CONTENT); // HttpStatus.SC_NO_CONTENT
//															// = 204
//			return;
//		}
//		chain.doFilter(req, response);
//	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		response.setHeader("Access-Control-Allow-Origin", httpServletRequest.getHeader("Origin"));
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers",
				"Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token,content-type");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Expose-Headers", "X-PINGOTHER, Origin,token, X-Requested-With, Content-Type, Accept,auth_token,Accept-Charset");
		// 这里通过判断请求的方法，判断此次是否是预检请求，如果是，立即返回一个204状态吗，标示，允许跨域；预检后，正式请求，这个方法参数就是我们设置的post了
		if ("OPTIONS".equals(httpServletRequest.getMethod())) {
			response.setStatus(HttpStatus.SC_NO_CONTENT); // HttpStatus.SC_NO_CONTENT
			// = 204
			return;
		}
		chain.doFilter(req, response);
	}

	@Override
	public void destroy() {

	}

//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//		response.setHeader("Access-Control-Max-Age", "3600");
//		response.setHeader("Access-Control-Allow-Headers",
//				"Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token,content-type");
//		response.setHeader("Access-Control-Allow-Credentials", "true");
//		response.setHeader("Access-Control-Expose-Headers", "X-PINGOTHER, Origin,token, X-Requested-With, Content-Type, Accept,auth_token,Accept-Charset");
//		// 这里通过判断请求的方法，判断此次是否是预检请求，如果是，立即返回一个204状态吗，标示，允许跨域；预检后，正式请求，这个方法参数就是我们设置的post了
//		if ("OPTIONS".equals(httpServletRequest.getMethod())) {
//			response.setStatus(HttpStatus.SC_NO_CONTENT); // HttpStatus.SC_NO_CONTENT
//			// = 204
//			return;
//		}
//		filterChain.doFilter(request, response);
//	}

//	@Override
//	public void init(FilterConfig config) throws ServletException {
//
//	}
}
