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

import com.google.common.base.Joiner;
import com.yofish.apollo.component.util.NamespaceKeyGenerator;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.InstanceConfigRepository;
import com.yofish.apollo.repository.ReleaseRepository;
import com.yofish.apollo.service.ItemService;
import com.yofish.gary.dao.entity.BaseEntity;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import framework.apollo.core.ConfigConsts;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.*;

import java.util.List;
import java.util.Objects;

import static com.yofish.gary.bean.StrategyNumBean.*;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:50
 */

@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 30)
public class AppEnvClusterNamespace extends BaseEntity {

    @ManyToOne(cascade = CascadeType.DETACH)
    private AppEnvCluster appEnvCluster;

    @ManyToOne(cascade = CascadeType.DETACH)
    private AppNamespace appNamespace;

    public AppEnvClusterNamespace(AppEnvCluster appEnvCluster, AppNamespace appNamespace) {
        if (isNamespaceUnique(appEnvCluster, appNamespace)) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "集群名称不能重复！");
        }
        this.appEnvCluster = appEnvCluster;
        this.appNamespace = appNamespace;

    }


    public Release findLatestActiveRelease() {

        return getBeanByClass(ReleaseRepository.class).findFirstByAppEnvClusterNamespace_IdAndAbandonedIsFalseOrderByIdDesc(this.getId());
    }

    public List<Release> findLatestActiveReleases(Pageable page) {
        return getBeanByClass(ReleaseRepository.class).findByAppEnvClusterNamespace_IdAndAbandonedIsFalseOrderByIdDesc(this.getId(), page);
    }

    public List<Item> getItems() {
        List<Item> items = getBeanByClass(ItemService.class).findItemsWithoutOrdered(this.getId());
        return items;
    }

    public Page<InstanceConfig> getInstanceConfigs(Pageable pageable) {
        Page<InstanceConfig> InstanceConfigPage = getBeanByClass(InstanceConfigRepository.class).findByNamespaceId(this.getId(), pageable);
        return InstanceConfigPage;
    }

    public int calcInstanceConfigsCount() {

        return getBeanByClass(InstanceConfigRepository.class).countByNamespaceId(this.getId());
    }


    public boolean isNamespaceUnique(AppEnvCluster appEnvCluster, AppNamespace appNamespace) {
        Objects.requireNonNull(appEnvCluster, "appEnvCluster must not be null");
        Objects.requireNonNull(appNamespace, "appNamespace must not be null");
        return Objects.isNull(getBeanByClass4Context(AppEnvClusterNamespaceRepository.class).findByAppEnvClusterAndAppNamespace(appEnvCluster, appNamespace));
    }

    public String generateNamespaceKey() {
        return NamespaceKeyGenerator.generate(this);
    }


    @Getter
    @AllArgsConstructor
    public enum Type {
        Main("main"),
        Branch("branch");

        private String value;
    }
}
