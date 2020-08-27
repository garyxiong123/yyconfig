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
package com.ctrip.framework.apollo.configservice.component.config;

import com.ctrip.framework.apollo.configservice.controller.ConfigFileController;
import com.ctrip.framework.apollo.configservice.pattern.listener.ReleaseMessageListener4Registry;
import com.ctrip.framework.apollo.configservice.cache.ReleaseMessageCache;
import com.ctrip.framework.apollo.configservice.controller.timer.sync.TimerTask4SyncReleaseMessage2Cache;
import com.ctrip.framework.apollo.configservice.component.ReleaseRepo;
import com.ctrip.framework.apollo.configservice.cache.ReleaseCache;
import com.ctrip.framework.apollo.configservice.controller.timer.ReleaseMessageScanner;
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
    //    @Autowired
//    private GrayReleaseRulesHolder grayReleaseRulesHolder;
    @Autowired
    private TimerTask4SyncReleaseMessage2Cache timerTask4SyncReleaseMessage2Cache;
    @Autowired
    private ReleaseMessageCache releaseMessageCache;
    @Autowired
    private ReleaseMessageListener4Registry releaseMessageListener4Registry;
    @Autowired
    private ReleaseCache releaseCache;

    @Bean
    public ReleaseMessageScanner releaseMessageScanner() {
        ReleaseMessageScanner releaseMessageScanner = new ReleaseMessageScanner();
        //0. handle release message cache
        releaseMessageScanner.addMessageListener(releaseMessageCache);


        //1. handle gray release rule
//      releaseMessageScanner.addMessageListener(grayReleaseRulesHolder);


        //2. handle config server cache
        releaseMessageScanner.addMessageListener(releaseCache);
        releaseMessageScanner.addMessageListener(configFileController);


        //3. notify clients
        releaseMessageScanner.addMessageListener(releaseMessageListener4Registry);
//            releaseMessageScanner.addMessageListener(notificationController);
        return releaseMessageScanner;
    }


    @Bean
    public ReleaseRepo configService() {
//        if (bizConfig.isConfigServiceCacheEnabled()) {
        return new ReleaseCache();
//        }
//        return new ReleaseRepo4NoCache();
    }
}