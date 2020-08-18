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
package com.ctrip.framework.apollo.configservice.config;

import com.ctrip.framework.apollo.configservice.controller.ConfigFileController;
import com.ctrip.framework.apollo.configservice.controller.ClientWatchRegisterController;
import com.ctrip.framework.apollo.configservice.controller.listener.WatchedKeyResultRepo;
import com.ctrip.framework.apollo.configservice.repo.ReleaseMessageServiceWithCache;
import com.ctrip.framework.apollo.configservice.pattern.strategy.loadRelease.ClientLoadReleaseStrategy;
import com.ctrip.framework.apollo.configservice.repo.ReleaseRepo4Cache;
import com.yofish.apollo.controller.timer.ReleaseMessageScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/27 下午1:35
 */
@Configuration
public class MessageScannerConfiguration {
    //        @Autowired
//        private NotificationController notificationController;
    @Autowired
    private ConfigFileController configFileController;
    @Autowired
    private ClientWatchRegisterController clientWatchRegisterController;
    //    @Autowired
//    private GrayReleaseRulesHolder grayReleaseRulesHolder;
    @Autowired
    private ReleaseMessageServiceWithCache releaseMessageServiceWithCache;
    @Autowired
    private ClientLoadReleaseStrategy clientLoadReleaseStrategy;
    @Autowired
    private WatchedKeyResultRepo watchedKeyResultRepo;
    @Autowired
    private ReleaseRepo4Cache releaseRepo4Cache;

    @Bean
    public ReleaseMessageScanner releaseMessageScanner() {
        ReleaseMessageScanner releaseMessageScanner = new ReleaseMessageScanner();
        //0. handle release message cache
        releaseMessageScanner.addMessageListener(releaseMessageServiceWithCache);


        //1. handle gray release rule
//      releaseMessageScanner.addMessageListener(grayReleaseRulesHolder);


        //2. handle server cache
        releaseMessageScanner.addMessageListener(releaseRepo4Cache);
        releaseMessageScanner.addMessageListener(configFileController);


        //3. notify clients
        releaseMessageScanner.addMessageListener(watchedKeyResultRepo);
//            releaseMessageScanner.addMessageListener(notificationController);
        return releaseMessageScanner;
    }
}