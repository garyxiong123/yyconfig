package com.yofish.apollo.domain;

import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:50
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class AppEnvClusterNamespace extends BaseEntity {

    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    private AppEnvCluster appEnvCluster;

    @ManyToOne(cascade = CascadeType.ALL)
    private AppNamespace namespace;
}
