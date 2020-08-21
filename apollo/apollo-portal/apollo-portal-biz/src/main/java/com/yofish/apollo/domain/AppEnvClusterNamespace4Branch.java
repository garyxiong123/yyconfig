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

import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Map;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/15 下午10:14
 */
@Data
@Entity
@DiscriminatorValue("branch")
public class AppEnvClusterNamespace4Branch extends AppEnvClusterNamespace {

    private Long parentId;

    @ManyToOne(cascade = CascadeType.DETACH)
    private GrayReleaseRule grayReleaseRule;


    public AppEnvClusterNamespace4Branch() {
    }

    public AppEnvClusterNamespace4Branch(Long parentId) {
        this.parentId = parentId;
    }


    public AppEnvClusterNamespace4Branch(AppEnvCluster appEnvCluster, AppNamespace appNamespace, String branchName) {
        super(appEnvCluster, appNamespace, branchName);
    }


    public AppEnvClusterNamespace4Main getMainNamespace() {
        AppEnvClusterNamespace4Main appEnvClusterNamespace = (AppEnvClusterNamespace4Main) getBeanByClass(AppEnvClusterNamespaceRepository.class).findById(parentId).orElseGet(null);
        return appEnvClusterNamespace;
    }

}
