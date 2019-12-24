package com.yofish.apollo.domain;

import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:49
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class Commit extends BaseEntity {

    private String name;

    @ManyToOne(cascade = CascadeType.DETACH)
    private AppEnvClusterNamespace appEnvClusterNamespace;


    @Lob
    @Column(name = "ChangeSets", nullable = false)
    private String changeSets;

    @Column(name = "Comment")
    private String comment;


}
