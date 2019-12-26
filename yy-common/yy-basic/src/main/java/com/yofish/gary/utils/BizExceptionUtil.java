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

import com.youyu.common.api.IBaseResultCode;
import com.youyu.common.exception.BizException;
import com.youyu.common.utils.YyAssert;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月28日 10:00:00
 * @work 业务异常工具类
 */
public class BizExceptionUtil extends YyAssert {

    /**
     * 抛出异常
     *
     * @param code
     * @param message
     */
    public static void exception(String code, String message) {
        throw new BizException(code, message);
    }

    /**
     * 抛出异常
     *
     * @param iBaseResultCode
     */
    public static void exception(IBaseResultCode iBaseResultCode) {
        exception(iBaseResultCode.getCode(), iBaseResultCode.getDesc());
    }

    /**
     * 匹配expression,则异常
     *
     * @param expression
     * @param code
     * @param message
     * @return
     */
    public static void exception2MatchingExpression(boolean expression, String code, String message) {
        if (expression) {
            exception(code, message);
        }
    }

    /**
     * 匹配expression,则异常
     *
     * @param expression
     * @param iBaseResultCode
     * @return
     */
    public static void exception2MatchingExpression(boolean expression, IBaseResultCode iBaseResultCode) {
        exception2MatchingExpression(expression, iBaseResultCode.getCode(), iBaseResultCode.getDesc());
    }

    /**
     * 不匹配expression,则异常
     *
     * @param expression
     * @param code
     * @param message
     * @return
     */
    public static void exception2MismatchingExpression(boolean expression, String code, String message) {
        exception2MatchingExpression(!expression, code, message);
    }

    /**
     * 不匹配expression,则异常
     *
     * @param expression
     * @param iBaseResultCode
     * @return
     */
    public static void exception2MismatchingExpression(boolean expression, IBaseResultCode iBaseResultCode) {
        exception2MismatchingExpression(expression, iBaseResultCode.getCode(), iBaseResultCode.getDesc());
    }
}
