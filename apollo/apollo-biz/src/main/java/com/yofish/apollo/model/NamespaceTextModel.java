package com.yofish.apollo.model;


import com.yofish.apollo.enums.ConfigFileFormat;
import com.yofish.apollo.enums.Envs;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class NamespaceTextModel {

    private String appId;
    private Envs envs;
    private String clusterName;
    private String namespaceName;
    private int namespaceId;
    private String format;
    private String configText;


    public ConfigFileFormat getFormat() {
        return ConfigFileFormat.fromString(this.format);
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
