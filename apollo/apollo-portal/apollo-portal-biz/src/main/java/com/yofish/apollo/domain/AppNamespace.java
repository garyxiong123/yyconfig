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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.gary.dao.entity.BaseEntity;
import framework.apollo.core.enums.ConfigFileFormat;
import lombok.*;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass;

/**
 * @author WangSongJun
 * @date 2019-12-02
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 30)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class AppNamespace extends BaseEntity {

    private String name;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.REMOVE})
    private App app;

    private ConfigFileFormat format;

    private String comment;

    public AppNamespace(Long id) {
        super(id);
    }

    public AppNamespace(Long id, String name, App app, ConfigFileFormat format, String comment) {
        super(id);
        this.name = name;
        this.app = app;
        this.format = ObjectUtils.isEmpty(format) ? ConfigFileFormat.Properties : format;
        this.comment = comment;
    }

    public boolean isPublicOrProtect() {
        return this instanceof AppNamespace4Public || this instanceof AppNamespace4Protect;
    }

    public AppEnvClusterNamespace getNamespaceByEnv(String env, String cluster,String type) {

        return getBeanByClass(AppEnvClusterNamespaceRepository.class).findAppEnvClusterNamespace(this.getApp().getAppCode(),env, this.name, cluster, type );
    }
}
