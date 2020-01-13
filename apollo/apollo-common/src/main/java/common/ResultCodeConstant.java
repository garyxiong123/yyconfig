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
package common;

/**
 * Description:
 * <p/>
 * <p> 如果是客户端错误,code必须以400开头
 * <p> 如果是服务端错误,code必须以500开头
 * <p/>
 *
 * @author Ping
 * @date 2018/5/7
 */
public interface ResultCodeConstant {

    String SUCCESS = "1";

    String COMMON_CODE_PREFIX = "0010000";

    String SYSTEM_ERROR = COMMON_CODE_PREFIX + "0000";

    String UNKNOWN_ERROR = COMMON_CODE_PREFIX + "0001";

    String HTTP_RESPONSE_ERROR = COMMON_CODE_PREFIX + "0010";

    String SMS_SUCCESS = "0000";
    /**
     * ops 系统 code 前缀
     */
    String COMMON_OPS_CODE_PREFIX = "00101";

    @Deprecated
    String METHOD_ARGUMENT_NOT_VALID = "4000001";

    @Deprecated
    String METHOD_ARGUMENT_TYPE_MISMATCH = "4000002";

    @Deprecated
    String HTTP_MESSAGE_NOT_READABLE = "4000003";


    @Deprecated
    String ILLEGAL_ARGUMENT = "4000004";

    @Deprecated
    String MISSING_SERVLET_REQUEST_PARAMETER = "4000005";


    /**
     * 远程服务404. BaseResultCode.UPLOAD_STATIC_DATA_FIND_FAILED
     */
    @Deprecated
    String SERVICE_UNAVAILABLE = "5000000";


}
