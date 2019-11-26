package common.exception;


import com.youyu.common.api.IBaseResultCode;
import com.youyu.common.exception.BizException;

/**
 * Created by xiongchengwei on 2018/8/17.
 */
public class YyIllegalArgumentException extends BizException {


    public YyIllegalArgumentException(IBaseResultCode iBaseResultCode) {
        super(iBaseResultCode);
    }

    public YyIllegalArgumentException(IBaseResultCode iBaseResultCode, String message) {
        super(iBaseResultCode, message);
    }
}