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

import com.google.gson.Gson;
import com.yofish.apollo.repository.ReleaseRepository;
import com.yofish.apollo.service.ReleaseHistoryService;
import com.yofish.apollo.service.ReleaseService;
import com.yofish.apollo.strategy.PublishStrategy;
import com.yofish.gary.dao.StrategyConverter;
import com.yofish.gary.dao.entity.BaseEntity;
import common.constants.ReleaseOperation;
import common.exception.NotFoundException;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

import static com.yofish.gary.bean.StrategyNumBean.getBeanInstance;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:49
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "releases")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 30)
public class Release extends BaseEntity {

    @Transient
    public Gson gson = new Gson();

    @Column(nullable = false)
    private String releaseKey;

    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = CascadeType.DETACH)
    private AppEnvClusterNamespace appEnvClusterNamespace;

    @Column(name = "Configurations")
    @Lob
    private String configurations;

    private boolean isEmergencyPublish;

    @Transient
    protected PublishStrategy publishStrategy;

    @Column(nullable = false)
    private boolean abandoned;

    @Column(name = "comment")
    private String comment;


    public Release(AppEnvClusterNamespace namespace, String name, String comment, Map<String, String> configurations, boolean isEmergencyPublish) {
        this.setName(name);
        this.setAppEnvClusterNamespace(namespace);
        this.isEmergencyPublish = isEmergencyPublish;
        this.setConfigurations(gson.toJson(configurations));
        this.comment = comment;
    }


    public Release publish() {

        return null;
    }


    public Release getPreviousRelease() {
        return null;

    }

    public String getAppCode() {
        return getAppEnvClusterNamespace().getAppNamespace().getApp().getAppCode();
    }
}
