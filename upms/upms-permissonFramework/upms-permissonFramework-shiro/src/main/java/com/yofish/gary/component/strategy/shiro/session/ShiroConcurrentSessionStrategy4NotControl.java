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
package com.yofish.gary.component.strategy.shiro.session;

import com.yofish.gary.annotation.StrategyNum;
import com.yofish.gary.biz.domain.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work shiro 并发session 不控制
 */
@Component
@StrategyNum(superClass = ShiroConcurrentSessionStrategy.class, number = "1", describe = "并发session 不控制")
public class ShiroConcurrentSessionStrategy4NotControl extends ShiroConcurrentSessionStrategy {

    @Override
    public void handleConcurrentSession() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            return;
        }

        doHandleConcurrentSession(subject);
    }

    /**
     * 执行处理并发session
     *
     * @param subject
     */
    private void doHandleConcurrentSession(Subject subject) {
        Session currentSession = subject.getSession();
        User user = (User) subject.getPrincipal();
        currentSession.setAttribute(currentSession.getId(), user.getId());
    }
}
