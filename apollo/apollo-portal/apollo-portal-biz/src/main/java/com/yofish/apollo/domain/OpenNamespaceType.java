package com.yofish.apollo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

/**
 * 公开namespace的类型
 *
 * @author WangSongJun
 * @date 2019-12-20
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class OpenNamespaceType extends BaseEntity {

    @Column(unique = true)
    private String name;

    private String comment;

    @OneToMany(cascade = CascadeType.DETACH)
    @JoinColumn(name = "namespaceTypeId")
    private Set<AppNamespace> appNamespaces;

    public OpenNamespaceType(Long id) {
        super(id);
    }

    @Builder
    public OpenNamespaceType(Long id, String name, String comment, Set<AppNamespace> appNamespaces) {
        super(id);
        this.name = name;
        this.comment = comment;
        this.appNamespaces = appNamespaces;
    }
}
