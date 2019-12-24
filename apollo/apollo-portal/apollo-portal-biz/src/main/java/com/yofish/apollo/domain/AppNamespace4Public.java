package com.yofish.apollo.domain;

import framework.apollo.core.enums.ConfigFileFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:50
 */

@NoArgsConstructor
@Data
@Entity
@DiscriminatorValue("Namespace4Public")
public class AppNamespace4Public extends AppNamespace {
    @Builder
    public AppNamespace4Public(String name, App app, ConfigFileFormat format, String comment, OpenNamespaceType openNamespaceType) {
        super(name, app, format, comment);
        this.openNamespaceType = openNamespaceType;
    }


    @ManyToOne(cascade = CascadeType.DETACH)
    private OpenNamespaceType openNamespaceType;
}
