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


import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import org.springframework.util.ObjectUtils;

public class RequestPrecondition {

    private static String CONTAIN_EMPTY_ARGUMENT = "request payload should not be contain empty.";

    private static String ILLEGAL_MODEL = "request model is invalid";

    private static String ILLEGAL_NUMBER = "number should be positive";


    public static void checkArgumentsNotEmpty(String... args) {
        checkArguments(!YyStringUtils.isContainEmpty(args), CONTAIN_EMPTY_ARGUMENT);
    }

    public static void checkArgumentsNotEmpty(Object... args) {
        checkArguments(!isContainEmpty(args), CONTAIN_EMPTY_ARGUMENT);
    }

    public static void checkModel(boolean valid) {
        checkArguments(valid, ILLEGAL_MODEL);
    }

    public static void checkArguments(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.valueOf(errorMessage));
        }
    }

    public static void checkNumberPositive(int... args) {
        for (int num : args) {
            if (num <= 0) {
                throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, ILLEGAL_NUMBER);
            }
        }
    }

    public static void checkNumberPositive(long... args) {
        for (long num : args) {
            if (num <= 0) {
                throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, ILLEGAL_NUMBER);
            }
        }
    }

    public static void checkNumberNotNegative(int... args) {
        for (int num : args) {
            if (num < 0) {
                throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, ILLEGAL_NUMBER);
            }
        }
    }

    private static boolean isContainEmpty(Object... objects) {
        if (ObjectUtils.isEmpty(objects)) {
            return true;
        }
        for (Object o : objects) {
            if (ObjectUtils.isEmpty(o)) {
                return true;
            }
        }
        return false;
    }

}
