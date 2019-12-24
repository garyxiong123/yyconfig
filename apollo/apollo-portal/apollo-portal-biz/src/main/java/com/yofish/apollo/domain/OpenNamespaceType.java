package com.yofish.apollo.domain;

import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
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
public class OpenNamespaceType extends BaseEntity {

    @Column(unique = true)
    private String name;

    private String comment;

    @OneToMany(cascade = CascadeType.DETACH)
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
