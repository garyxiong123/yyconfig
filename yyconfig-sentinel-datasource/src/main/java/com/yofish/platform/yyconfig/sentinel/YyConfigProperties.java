package com.yofish.platform.yyconfig.sentinel;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * SentinelProperties
 *
 * @author WangSongJun
 * @date 2021-12-09
 */
@ConfigurationProperties(
        prefix = "spring.cloud.sentinel.yyconfig"
)
public class YyConfigProperties {

    private Map<String, YyConfigDataSourceProperties> datasource;

    public Map<String, YyConfigDataSourceProperties> getDatasource() {
        return datasource;
    }

    public void setDatasource(Map<String, YyConfigDataSourceProperties> datasource) {
        this.datasource = datasource;
    }
}
