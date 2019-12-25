package com.yofish.apollo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import framework.apollo.core.enums.ConfigFileFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:50
 */

@NoArgsConstructor
@Data
@Entity
@DiscriminatorValue("Namespace4Protect")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class AppNamespace4Protect extends AppNamespace {
    @Builder
    public AppNamespace4Protect(String name, App app, ConfigFileFormat format, String comment, Set<App> authorizedApp, OpenNamespaceType openNamespaceType) {
        super(name, app, format, comment);
        this.authorizedApp = authorizedApp;
        this.openNamespaceType = openNamespaceType;
    }

    @ManyToMany(cascade = CascadeType.DETACH)
    private Set<App> authorizedApp;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "namespaceTypeId")
    private OpenNamespaceType openNamespaceType;
}
