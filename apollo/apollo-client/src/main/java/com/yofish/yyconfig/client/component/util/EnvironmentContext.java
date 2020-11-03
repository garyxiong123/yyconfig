package com.yofish.yyconfig.client.component.util;

import org.springframework.core.env.Environment;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明
 * @Date: 2020/8/20 下午8:24
 */
public class EnvironmentContext {

    public static Environment environment;

    public static void setEnvironment(Environment environment1){
        environment = environment1;
    }


}
