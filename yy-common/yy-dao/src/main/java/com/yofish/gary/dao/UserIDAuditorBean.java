package com.yofish.gary.dao;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/11 上午11:31
 */

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class UserIDAuditorBean implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("张三");
    }
}