package com.yofish.gary.dao;

import org.reflections.Reflections;

import javax.persistence.DiscriminatorValue;
import java.util.Set;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/16 下午4:47
 */
public class DiscriminatorUtil {

    public static Class findChildByDiscriminatorType(String type, Class zClass1) {
//        Reflections reflections = new Reflections(zClass1);
//        Set<Class<? extends zClass1>> subTypes = reflections.getSubTypesOf(zClass1);
//        for (Class<? extends zClass1> subClass : subTypes) {
//            if (type.equals(subClass.getAnnotation(DiscriminatorValue.class).value())) {
//                return subClass;
//            }
//        }
        return null;
    }
}
