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
package com.yofish.gary.bean;

import com.yofish.gary.annotation.Bridge;
import com.yofish.gary.annotation.BridgeAutowired;
import com.yofish.gary.annotation.BridgeListAutowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static com.yofish.gary.bean.StrategyNumBean.getBeanInstance;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年7月17日 17:00:00
 * @work 桥接注解Bridge解析
 */
@Slf4j
public class BridgeBean implements ApplicationContextAware, InitializingBean {

    /**
     * 容器上下文
     */
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, Object> bridgeMap = applicationContext.getBeansWithAnnotation(Bridge.class);
        bridgeAutowired(bridgeMap);
    }

    /**
     * 桥接注入
     *
     * @param bridgeMap
     */
    private void bridgeAutowired(Map<String, Object> bridgeMap) {
        if (isEmpty(bridgeMap)) {
            return;
        }

        for (Map.Entry<String, Object> entry : bridgeMap.entrySet()) {
            doBridgeAutowired(entry);
        }
    }

    /**
     * 执行桥接注入
     *
     * @param entry
     */
    private void doBridgeAutowired(Map.Entry<String, Object> entry) {
        Object bean = entry.getValue();
        Method[] declaredMethods = bean.getClass().getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            invokeDeclaredMethod(bean, declaredMethod);
        }

    }

    /**
     * 反射执行含有注解(BridgeListAutowired,BridgeAutowired)对应的method方法调用
     * 注:暂不做抽象通用,且BridgeListAutowired和BridgeAutowired不支持共存
     *
     * @param bean
     * @param method
     * @see BridgeListAutowired , BridgeAutowired
     */
    private void invokeDeclaredMethod(Object bean, Method method) {
        Bridge bridge = findAnnotation(bean.getClass(), Bridge.class);

        BridgeListAutowired bridgeListAutowired = findAnnotation(method, BridgeListAutowired.class);
        if (nonNull(bridgeListAutowired)) {
            try {
                method.invoke(bean, getListParam(bridgeListAutowired.bridgeValues(), bridge));
            } catch (Exception ex) {
                log.error("反射执行含有BridgeListAutowired注解的方法异常信息:[{}]", getFullStackTrace(ex));
            }
            return;
        }

        BridgeAutowired bridgeAutowired = findAnnotation(method, BridgeAutowired.class);
        if (nonNull(bridgeAutowired)) {
            try {
                method.invoke(bean, getSingleParam(bridgeAutowired.bridgeValue(), bridge));
            } catch (Exception ex) {
                log.error("反射执行含有BridgeAutowired注解的方法异常信息:[{}]", getFullStackTrace(ex));
            }
            return;
        }
    }

    /**
     * 获取列表List类型参数
     *
     * @param bridgeValues
     * @param bridge
     * @param <T>
     * @return
     */
    private <T> List<T> getListParam(String[] bridgeValues, Bridge bridge) {
        List<T> list = new ArrayList<>();
        for (String bridgeValue : bridgeValues) {
            list.add((T) getBeanInstance(bridge.associationClass(), bridgeValue));
        }
        return list;
    }

    /**
     * 获取单个类型参数
     *
     * @param bridgeValue
     * @param bridge
     * @return
     */
    private Object getSingleParam(String bridgeValue, Bridge bridge) {
        return getBeanInstance(bridge.associationClass(), bridgeValue);
    }
}
