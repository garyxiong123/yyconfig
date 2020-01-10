/*
 *    Copyright 2019-2020 the original author or authors.
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
