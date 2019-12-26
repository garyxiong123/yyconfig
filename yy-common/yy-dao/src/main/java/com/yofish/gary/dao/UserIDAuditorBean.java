package com.yofish.gary.dao;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/11 上午11:31
 */

import com.yofish.gary.subject.YyThreadLocalSubject;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
public class UserIDAuditorBean implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        if (YyThreadLocalSubject.getOpsUser() == null) {
            return Optional.of("初始用户");
        }
        return Optional.of(YyThreadLocalSubject.getOpsUser().getUsername());
    }
}