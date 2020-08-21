/*
 *    Copyright 2019-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.yofish.apollo.domain;

import com.yofish.apollo.repository.AppEnvClusterRepository;
import com.yofish.gary.dao.entity.BaseEntity;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import lombok.*;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import java.util.Objects;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass;

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
        isClusterNameUnique();
    }

    public void isClusterNameUnique() {
        Objects.requireNonNull(app.getId(), "AppId must not be null");
        Objects.requireNonNull(name, "ClusterName must not be null");
        if (!ObjectUtils.isEmpty((getBeanByClass(AppEnvClusterRepository.class).findClusterByAppIdAndEnvAndName(app.getId(), env, name)))) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "clusterEntity not unique");

        }
    }
}
