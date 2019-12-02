package com.yofish.apollo.domain;

import com.yofish.gary.dao.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@NoArgsConstructor
@AllArgsConstructor
public class AppNamespace extends BaseEntity {

    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    private App app;

    private String format;

    private boolean isPublic = false;

    private String comment;
}
