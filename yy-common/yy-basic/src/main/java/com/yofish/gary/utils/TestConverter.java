package com.yofish.gary.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/10 下午3:25
 */
@Component
public class TestConverter implements Converter<String, Object> {


    @Override
    public Object convert(String source) {
        return null;
    }
}
