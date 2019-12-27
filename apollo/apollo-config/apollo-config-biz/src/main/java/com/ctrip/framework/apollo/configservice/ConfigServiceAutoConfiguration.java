package com.ctrip.framework.apollo.configservice;

import com.ctrip.framework.apollo.configservice.controller.ConfigFileController;
import com.ctrip.framework.apollo.configservice.controller.NotificationController;
import com.ctrip.framework.apollo.configservice.controller.NotificationControllerV2;
import com.ctrip.framework.apollo.configservice.service.ReleaseMessageServiceWithCache;
import com.ctrip.framework.apollo.configservice.service.config.ConfigService;
import com.ctrip.framework.apollo.configservice.service.config.ConfigServiceWithCache;
import com.ctrip.framework.apollo.configservice.service.config.DefaultConfigService;
import com.yofish.apollo.message.ReleaseMessageScanner;
import com.yofish.apollo.service.PortalConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Configuration
public class ConfigServiceAutoConfiguration {

    @Autowired
    private PortalConfig bizConfig;

//  @Bean
//  public GrayReleaseRulesHolder grayReleaseRulesHolder() {
//    return new GrayReleaseRulesHolder();
//  }

    @Bean
    public ConfigService configService() {
        if (bizConfig.isConfigServiceCacheEnabled()) {
            return new ConfigServiceWithCache();
        }
        return new DefaultConfigService();
    }



}
