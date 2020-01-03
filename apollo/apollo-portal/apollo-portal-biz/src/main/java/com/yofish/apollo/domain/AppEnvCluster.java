package com.yofish.apollo.domain;

import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:50
 */

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
//@Table( uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "env", "app_id"})})
public class AppEnvCluster extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "env")
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
