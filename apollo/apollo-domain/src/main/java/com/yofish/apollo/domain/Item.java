package com.yofish.apollo.domain;

import com.yofish.apollo.dto.CreateItemReq;
import com.yofish.apollo.dto.UpdateItemReq;
import com.yofish.gary.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:51
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class Item extends BaseEntity {

    @ManyToOne(cascade = CascadeType.ALL)
    private ClusterNamespace clusterNamespace;

    @Column(name = "keynumber", nullable = false)
    private String key;

    @Column(name = "value")
    @Lob
    private String value;

    @Column(name = "commentdesc", nullable = false)
    private String comment;

    private Integer lineNum;

    public Item(CreateItemReq createItemReq) {

    }

    public Item(UpdateItemReq updateItemReq) {

    }
}
