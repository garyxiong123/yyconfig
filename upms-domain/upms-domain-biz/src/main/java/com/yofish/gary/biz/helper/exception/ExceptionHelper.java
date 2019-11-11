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
package com.yofish.gary.biz.helper.exception;

import com.youyu.common.exception.BizException;


/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 异常帮助类
 */
public class ExceptionHelper {

    /**
     * 登录异常
     *
     * @param ex
     * @return
     */
    public static BizException loginException(Exception ex) {
        Throwable throwable = ex.getCause();
        boolean bizExceptionFlag = throwable instanceof BizException;
        return bizExceptionFlag ? (BizException) throwable : new BizException("登陆异常");
    }
}
