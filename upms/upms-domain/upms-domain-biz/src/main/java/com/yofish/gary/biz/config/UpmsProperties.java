package com.yofish.gary.biz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author WangSongJun
 * @date 2020-01-03
 */
@Data
@Component
@ConfigurationProperties(prefix = "upms")
public class UpmsProperties {
    private InitProperties init = new InitProperties();
}
