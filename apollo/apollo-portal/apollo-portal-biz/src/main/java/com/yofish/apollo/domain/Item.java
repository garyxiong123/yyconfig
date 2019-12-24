package com.yofish.apollo.domain;

import com.yofish.apollo.dto.CreateItemReq;
import com.yofish.apollo.dto.UpdateItemReq;
import com.yofish.gary.dao.entity.BaseEntity;
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

    @ManyToOne(cascade = CascadeType.DETACH)
    private AppEnvClusterNamespace appEnvClusterNamespace;

    @Column(name = "keynumber", nullable = false)
    private String key;

    @Column(name = "value")
    @Lob
    private String value;

    @Column(name = "comment_desc")
    private String comment;

    private Integer lineNum;

    public Item(String key,String value,String comment,AppEnvClusterNamespace a,Integer lineNum) {
        this.key=key;
        this.value=value;
        this.comment=comment;
        this.appEnvClusterNamespace=a;
        this.lineNum=lineNum;
    }

    public Item(UpdateItemReq updateItemReq) {

    }
}
