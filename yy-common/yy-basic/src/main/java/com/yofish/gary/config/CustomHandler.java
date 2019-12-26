package com.yofish.gary.config;

import com.yofish.gary.annotation.StrategyNum;
import com.yofish.gary.bean.SpringBean;
import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static com.yofish.gary.utils.StringUtil.checkNonBlank;
import static org.apache.commons.lang3.StringUtils.join;

/**
 * @author panqingqing
 * @version v1.0
 * @date 2018年12月5日 下午10:00:00
 * @work 自定义Handler启动加载工厂
 */
public class CustomHandler implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    private final static Map<String, Object> BEAN_FACTORY_MAP = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        scanCustomAnnotations(StrategyNum.class, SpringBean.class);

        scanSpringAnnotations(Service.class, Repository.class);
    }

    /**
     * 扫描自定义注解
     *
     * @param tClasses
     */
    private void scanCustomAnnotations(Class<? extends Annotation>... tClasses) {
        for (Class tClass : tClasses) {
            Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(tClass);
            for (Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
                CustomHandlerEnum customHandlerEnum = CustomHandlerEnum.getCustomHandlerEnum(tClass);
                customHandlerEnum.loadAnnotation2MapFactory(entry);
            }
        }
    }

    /**
     * 扫描spring提供注解
     *
     * @param tClasses
     */
    private void scanSpringAnnotations(Class<? extends Annotation>... tClasses) {
        for (Class<? extends Annotation> tClass : tClasses) {
            Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(tClass);
            for (Map.Entry<String, Object> entry : beansWithAnnotation.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                Scope scope = value.getClass().getAnnotation(Scope.class);
                if (scope == null) {
                    BEAN_FACTORY_MAP.put(key, value);
                    BEAN_FACTORY_MAP.put(key.toLowerCase(), value);
                }
            }
        }
    }

    /**
     * 根据bean class获取bean
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T getBeanByClass(Class<T> tClass) {
        requireNonNull(tClass, "请传入正确的Class的类:[" + tClass + "]!");
        String simpleName = tClass.getSimpleName().toLowerCase();
        return (T) getBeanByName(simpleName);
    }

    /**
     * 根据bean name获取bean
     *
     * @param beanName
     * @return
     */
    public static Object getBeanByName(String beanName) {
        checkNonBlank(beanName, "请传入正确的Bean的名称:[" + beanName + "]!");
        Object bean = requireNonNull(BEAN_FACTORY_MAP.get(beanName), "根据beanName:[" + beanName + "]未查询到该Bean!");
        return bean;
    }

    /**
     * 获取Spring Bean 实例
     *
     * @param tClass
     * @param parameters
     * @param <T>
     * @return
     */
    public static <T> T getBeanInstance(Class<T> tClass, String... parameters) {
        requireNonNull(tClass, "请传入正确的Class的类:[" + tClass + "]!");
        String parameter = join(parameters);
        return (T) getBeanByName(join(tClass.getSimpleName(), parameter));
    }

    /**
     * 需要bean不存在才可以加载
     *
     * @param parameters
     */
    private static void requireBeanNotExist(String... parameters) {
        for (String parameter : parameters) {
            Object bean = BEAN_FACTORY_MAP.get(parameter);
            if (nonNull(bean)) {
                throw new RuntimeException("存在key=[" + parameter + "]对应的单例对象!");
            }
        }
    }

    @Getter
    private enum CustomHandlerEnum {
        STATUS_AND_STRATEGY_NUM("001", "状态策略枚举", StrategyNum.class) {
            @Override
            void loadAnnotation2MapFactory(Map.Entry<String, Object> entry) {
                Object object = entry.getValue();
                Class<?> aClass = object.getClass();
                StrategyNum statusAndStrategyNum = (StrategyNum) aClass.getAnnotation(getAClass());
                String simpleName = aClass.getSimpleName().toLowerCase();

                String number = statusAndStrategyNum.number();
                Class<?> superClass = statusAndStrategyNum.superClass();
                requireNonNull(superClass, "StatusAndStrategyNum注解的superClass属性不能为null!");
                String key = superClass.getSimpleName() + number;

                requireBeanNotExist(simpleName, key);
                BEAN_FACTORY_MAP.put(simpleName, object);
                BEAN_FACTORY_MAP.put(key, object);
            }
        },
        SPRING_BEAN("002", "SpringBean注解", SpringBean.class) {
            @Override
            void loadAnnotation2MapFactory(Map.Entry<String, Object> entry) {
                String key = entry.getKey();
                String keyLowerCase = entry.getKey().toLowerCase();
                Object object = entry.getValue();

                requireBeanNotExist(key, keyLowerCase);
                BEAN_FACTORY_MAP.put(key, object);
                BEAN_FACTORY_MAP.put(keyLowerCase, object);
            }
        };

        private String code;

        private String desc;

        private Class<? extends Annotation> aClass;

        CustomHandlerEnum(String code, String desc, Class<? extends Annotation> aClass) {
            this.code = code;
            this.desc = desc;
            this.aClass = aClass;
        }

        /**
         * 获取CustomHandlerEnum枚举
         *
         * @param aClass
         * @return
         */
        public static CustomHandlerEnum getCustomHandlerEnum(Class<?> aClass) {
            CustomHandlerEnum[] customHandlerEnums = values();
            for (CustomHandlerEnum customHandlerEnum : customHandlerEnums) {
                Class<?> enumAClass = customHandlerEnum.getAClass();
                if (enumAClass == aClass) {
                    return customHandlerEnum;
                }
            }
            throw new RuntimeException("未找到Class:[" + aClass + "]对应的CustomHandlerEnum枚举!");
        }

        /**
         * 差异化装载
         *
         * @param entry
         */
        abstract void loadAnnotation2MapFactory(Map.Entry<String, Object> entry);
    }
}
