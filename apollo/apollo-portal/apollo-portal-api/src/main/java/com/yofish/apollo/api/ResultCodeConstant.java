package com.yofish.apollo.api;

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
