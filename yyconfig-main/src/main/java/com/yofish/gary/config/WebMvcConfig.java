package com.yofish.gary.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/5 下午12:15
 */
//将一个物理类变成一个配置文件
@Configuration
//适配器
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    /**
     * 利用fastjson替换掉jackson,且解决中文乱码问题
     * @param  converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //1.构建了一个HttpMessageConverter  FastJson   消息转换器
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        //2.定义一个配置，设置编码方式，和格式化的形式
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        //3.设置成了PrettyFormat格式
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        //4.处理中文乱码问题
        List<MediaType> fastMediaTypes =  new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);

        //5.将fastJsonConfig加到消息转换器中
        fastConverter.setFastJsonConfig(fastJsonConfig);
        converters.add(fastConverter);
    }
}