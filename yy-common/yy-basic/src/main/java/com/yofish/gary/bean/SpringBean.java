package com.yofish.gary.bean;

import java.lang.annotation.*;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2018年12月5日 下午10:00:00
 * @work SpringBean注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SpringBean {

}
