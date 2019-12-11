package com.yofish.apollo.model.model;

import com.yofish.apollo.enums.NamespaceType;
import framework.apollo.core.enums.ConfigFileFormat;
import lombok.Data;

/**
 * @author WangSongJun
 * @date 2019-12-11
 */
@Data
public class AppNamespaceModel {

    private String name;

    private Long appId;

    private ConfigFileFormat format;

    private NamespaceType type;

    private String comment;
}
