package com.yofish.apollo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import framework.apollo.core.enums.ConfigFileFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:50
 */

@NoArgsConstructor
@Data
@Entity
@DiscriminatorValue("Namespace4Public")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class AppNamespace4Public extends AppNamespace {
    @Builder
    public AppNamespace4Public(String name, App app, ConfigFileFormat format, String comment, OpenNamespaceType openNamespaceType) {
        super(name, app, format, comment);
        this.openNamespaceType = openNamespaceType;
    }

    public AppNamespace4Public(Long id) {
        super(id);
    }

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "namespaceTypeId")
    private OpenNamespaceType openNamespaceType;
}
