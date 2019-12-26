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
package com.yofish.gary.utils;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月28日 10:00:00
 * @work orika属性拷贝
 */
public class OrikaCopyUtil {

    /**
     * 对象转换器
     */
    private static MapperFacade mapperFacade = new DefaultMapperFactory.Builder().build().getMapperFacade();

    /**
     * 将source转化为destination
     *
     * @param source
     * @param destination
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> T copyProperty(S source, Class<T> destination) {
        if (isNull(source)) {
            return null;
        }

        return mapperFacade.map(source, destination);
    }

    /**
     * 将将sources转化为destinations
     *
     * @param sources
     * @param destination
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> List<T> copyProperty4List(List<S> sources, Class<T> destination) {
        if (CollectionUtils.isEmpty(sources)) {
            return null;
        }

        List<T> list = new ArrayList<>();
        for (S source : sources) {
            list.add(copyProperty(source, destination));
        }
        return list;
    }

    /**
     * 将将sources转化为destinations
     *
     * @param sources
     * @param destination
     * @param <S>
     * @param <T>
     * @return
     */
    public static <S, T> List<T> copyProperty4Array(S[] sources, Class<T> destination) {
        if (ObjectUtils.isEmpty(sources)) {
            return null;
        }

        List<T> list = new ArrayList<>();
        for (S source : sources) {
            list.add(copyProperty(source, destination));
        }
        return list;
    }
}
