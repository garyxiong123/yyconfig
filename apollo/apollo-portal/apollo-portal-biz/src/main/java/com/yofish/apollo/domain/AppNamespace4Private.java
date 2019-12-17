package com.yofish.apollo.domain;

import framework.apollo.core.enums.ConfigFileFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:50
 */

@NoArgsConstructor
@Data
@Entity
@DiscriminatorValue("Namespace4Private")
public class AppNamespace4Private extends AppNamespace {
    @Builder
    public AppNamespace4Private(String name, App app, ConfigFileFormat format, String comment) {
        super(name, app, format, comment);
    }
}
