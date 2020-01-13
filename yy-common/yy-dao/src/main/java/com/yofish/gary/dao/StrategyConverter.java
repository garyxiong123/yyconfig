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

import com.yofish.gary.bean.StrategyNumBean;
import com.yofish.gary.annotation.StrategyNum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;

import static java.util.Objects.isNull;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/10 下午3:25
 */
@Component
public class StrategyConverter implements AttributeConverter<Object, String> ,Converter<String, Object> {


    @Override
    public String convertToDatabaseColumn(Object attribute) {
        if(isNull(attribute)){
            return null;
        }
        StrategyNum strategyNum = findAnnotation(attribute.getClass(), StrategyNum.class);
        String combineKey = StrategyNumBean.getCombineKey(strategyNum);
        return combineKey;
    }

    @Override
    public Object convertToEntityAttribute(String combineKey) {
        if (isBlank(combineKey)) {
            return null;
        }
        return StrategyNumBean.getBeanByName(combineKey);
    }

    @Override
    public Object convert(String source) {
        return null;
    }
}
