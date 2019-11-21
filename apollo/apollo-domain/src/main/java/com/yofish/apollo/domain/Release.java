package com.yofish.apollo.domain;

import com.yofish.gary.entity.BaseEntity;
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
public class Release extends BaseEntity {


    @Column(nullable = false)
    private String releaseKey;

    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    private ClusterNamespace clusterNamespace;

    @Column(name = "Configurations", nullable = false)
    @Lob
    private String configurations;

    @Column(name = "Comment", nullable = false)
    private String comment;


}
