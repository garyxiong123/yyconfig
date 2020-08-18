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

import com.ctrip.framework.apollo.configservice.pattern.strategy.loadRelease.ClientLoadReleaseStrategy;
import com.ctrip.framework.apollo.configservice.pattern.strategy.loadRelease.ClientLoadReleaseStrategy4Normal;
import com.ctrip.framework.apollo.configservice.repo.ReleaseRepo;
import com.ctrip.framework.apollo.configservice.repo.ReleaseRepo4Cache;
import com.ctrip.framework.apollo.configservice.repo.ReleaseRepo4NoCache;
import com.yofish.apollo.service.PortalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Configuration
public class ConfigServiceAutoConfiguration {

//    @Autowired
//    private PortalConfig bizConfig;

//  @Bean
//  public GrayReleaseRulesHolder grayReleaseRulesHolder() {
//    return new GrayReleaseRulesHolder();
//  }

    @Bean
    public ReleaseRepo configService() {
//        if (bizConfig.isConfigServiceCacheEnabled()) {
            return new ReleaseRepo4Cache();
//        }
//        return new ReleaseRepo4NoCache();
    }


}
