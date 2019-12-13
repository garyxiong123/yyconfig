package com.yofish.apollo.domain;

import com.yofish.gary.dao.entity.BaseEntity;
import framework.apollo.core.enums.ConfigFileFormat;
import lombok.*;

import javax.persistence.*;

/**
 * @author WangSongJun
 * @date 2019-12-02
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 30)
public class AppNamespace extends BaseEntity {

    private String name;

    @ManyToOne(cascade = {CascadeType.DETACH,CascadeType.REMOVE})
    private App app;

    private ConfigFileFormat format;

    private String comment;

    public AppNamespace(Long id, String name, App app, ConfigFileFormat format, String comment) {
        super(id);
        this.name = name;
        this.app = app;
        this.format = format;
        this.comment = comment;
    }

    public boolean isPublic() {
        return false;
    }
}
