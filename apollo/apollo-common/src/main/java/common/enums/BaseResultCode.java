package common.enums;


import com.youyu.common.api.IBaseResultCode;
import org.springframework.util.ObjectUtils;

/**
 */
public enum BaseResultCode implements IBaseResultCode {


    SUCCESS(ResultCodeConstant.SUCCESS, "success"),

    LOGIN_FAIL("1000", "success"),
    //系统错误
    SYSTEM_ERROR(ResultCodeConstant.SYSTEM_ERROR, "系统错误"),


    //系统错误-未知错误(00100000001)
    UNKNOWN_ERROR(ResultCodeConstant.UNKNOWN_ERROR, "未知错误"),

    //内部业务错误
    BUSINESS_ERROR(ResultCodeConstant.COMMON_CODE_PREFIX + "1000", "business error."),
    //客户端错误-数据格式错误(60053****)
    REQUEST_PARAMS_WRONG(ResultCodeConstant.COMMON_CODE_PREFIX + "2000", "请求参数不合法"),
    LOST_REQUEST_PARAMS(ResultCodeConstant.COMMON_CODE_PREFIX + "2001", "参数不能为空"),
    MISSING_SERVLET_REQUEST_PARAMETER(ResultCodeConstant.COMMON_CODE_PREFIX + "2005", "缺少参数"),
    REQUEST_METHOD_WRONG(ResultCodeConstant.COMMON_CODE_PREFIX + "2006", "请求方法错误"),
    REQUEST_MEDIA_WRONG(ResultCodeConstant.COMMON_CODE_PREFIX + "2007", "请求类型错误"),
    //内部业务错误-数据错误(60091****)
    REQUEST_PARAMS_TYPE_WRONG(ResultCodeConstant.COMMON_CODE_PREFIX + "3000", "参数类型不合法"),
    NO_DATA_FOUND(ResultCodeConstant.COMMON_CODE_PREFIX + "3001", "没有满足条件的数据返回"),
    UPLOAD_STATIC_DATA_FIND_FAILED(ResultCodeConstant.COMMON_CODE_PREFIX + "3002", "获取resourseServer数据失败"),

    //系统错误-数据库错误(60021****)
    DB_OPERATION_ERROR(ResultCodeConstant.COMMON_CODE_PREFIX + "4000", "数据库操作失败"),
    DB_SELECT_ERROR(ResultCodeConstant.COMMON_CODE_PREFIX + "4001", "数据库查询数据失败"),
    DB_INSERT_ERROR(ResultCodeConstant.COMMON_CODE_PREFIX + "4002", "数据库插入数据失败."),
    DB_UPDATE_ERROR(ResultCodeConstant.COMMON_CODE_PREFIX + "4003", "数据库更新数据失败."),
    DB_DELETE_ERROR(ResultCodeConstant.COMMON_CODE_PREFIX + "4004", "数据库删除数据失败."),
    DB_ACCESS_ERROR(ResultCodeConstant.COMMON_CODE_PREFIX + "4005", "数据库访问异常"),
    DB_DATA_ERROR(ResultCodeConstant.COMMON_CODE_PREFIX + "4006", "数据库数据错误"),
    DB_DUPLICATEKEY_ERROR(ResultCodeConstant.COMMON_CODE_PREFIX + "4007", "数据库字段数据重复添加");

    /**
     * 返回错误码
     **/
    private String code;
    /**
     * 返回错误信息
     **/
    private String desc;


    BaseResultCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }


    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static IBaseResultCode byCode(String code) {
        if (ObjectUtils.isEmpty(code)) {
            return null;
        }

        for (IBaseResultCode e : values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }

}
