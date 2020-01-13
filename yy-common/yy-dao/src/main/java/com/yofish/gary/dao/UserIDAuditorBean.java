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