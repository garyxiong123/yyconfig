package com.yofish.apollo.domain;

import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.List;

/**
 * 公开的namespace类型
 *
 * @author WangSongJun
 * @date 2019-12-20
 */
@Data
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class OpenNamespaceType extends BaseEntity {

    private String name;

    private String comment;

    @ManyToMany
    private List<AppNamespace> appNamespaces;
}
