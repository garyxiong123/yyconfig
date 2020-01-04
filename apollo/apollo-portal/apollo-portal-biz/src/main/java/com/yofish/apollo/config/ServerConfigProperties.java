package com.yofish.apollo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author WangSongJun
 * @date 2020-01-02
 */
@Component
@ConfigurationProperties(prefix = "apollo")
public class ServerConfigProperties {
    @Setter
    private Map<SystemInitConfigKey, String> initConfig;

    @Setter
    @Getter
    private Map<ServerConfigKey, String> serverConfig;

    @Autowired
    private ConfigurableEnvironment environment;

    public String get(SystemInitConfigKey key) {
        String property = environment.getProperty(key.name());
        return StringUtils.hasText(property) ? property : initConfig.get(key);
    }

    public String get(ServerConfigKey key) {
        String property = environment.getProperty(key.name());
        return StringUtils.hasText(property) ? property : serverConfig.get(key);
    }

}
