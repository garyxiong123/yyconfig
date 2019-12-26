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
@Setter
@Getter
@Entity
public class AppEnvCluster extends BaseEntity {

    private String name;

    private String env;

    @ManyToOne(cascade = CascadeType.DETACH)
    private App app;

    public AppEnvCluster(Long id) {
        super(id);
    }

    @Builder
    public AppEnvCluster(Long id, String name, String env, App app) {
        super(id);
        this.name = name;
        this.env = env;
        this.app = app;
    }
}
