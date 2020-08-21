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

import com.yofish.apollo.repository.AppEnvClusterNamespace4BranchRepository;
import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import static com.yofish.gary.bean.StrategyNumBean.getBeanInstance;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/15 下午10:14
 */
@Data
@Entity
@DiscriminatorValue("main")
public class AppEnvClusterNamespace4Main extends AppEnvClusterNamespace {

    public AppEnvClusterNamespace4Main(AppEnvCluster appEnvCluster, AppNamespace appNamespace, String branchName) {
        super(appEnvCluster, appNamespace, branchName);
    }

    public AppEnvClusterNamespace4Main() {

    }

    public AppEnvClusterNamespace4Branch getBranchNamespace() {
        return getBeanInstance(AppEnvClusterNamespace4BranchRepository.class).findByParentId(this.getId());
    }

    public boolean hasBranchNamespace() {
        return getBeanInstance(AppEnvClusterNamespace4BranchRepository.class).findByParentId(this.getId()) != null;
    }


}
