package com.yofish.apollo.domain;

import com.yofish.apollo.enums.NamespaceType;
import com.yofish.gary.dao.entity.BaseEntity;
import framework.apollo.core.enums.ConfigFileFormat;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @author WangSongJun
 * @date 2019-12-02
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppNamespace extends BaseEntity {

    private String name;

    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.REMOVE})
    private App app;

    private ConfigFileFormat format;

    private NamespaceType type;

    private String comment;
}
