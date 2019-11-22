package com.yofish.apollo.domain;

import com.yofish.gary.biz.domain.User;
import com.yofish.gary.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

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

    @ManyToOne(cascade = CascadeType.ALL)
    private ClusterNamespace clusterNamespace;


    @Lob
    @Column(name = "ChangeSets", nullable = false)
    private String changeSets;

    @Column(name = "Comment")
    private String comment;


}
