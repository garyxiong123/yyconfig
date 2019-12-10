package com.yofish.apollo.domain;

import com.yofish.gary.dao.entity.BaseEntity;
import framework.apollo.core.enums.Env;
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
public class Cluster extends BaseEntity {

    private String name;

    private String env;

    @ManyToOne(cascade = CascadeType.ALL)
    private App app;
}
