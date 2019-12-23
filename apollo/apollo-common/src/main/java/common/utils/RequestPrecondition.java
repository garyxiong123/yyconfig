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
