package common.condition;

import java.lang.annotation.*;

/**
 * created by zhangyingbin on 2019/12/18 0018 上午 10:40
 * description:
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PermissionAuth {

    PermissionType value() default PermissionType.user;

    enum PermissionType{
        user,           //普通用户
        participant,    //参与者
        leader,         //负责人
        admin           //管理员
    }

}
