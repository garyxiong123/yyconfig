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

import com.google.common.base.Strings;
import com.yofish.apollo.repository.InstanceRepository;
import com.yofish.apollo.repository.ReleaseRepository;
import com.yofish.gary.dao.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Instance extends BaseEntity {
    @ManyToOne
    private AppEnvCluster appEnvCluster;

    @Column(name = "DataCenter", nullable = false)
    private String dataCenter;

    @Column(name = "Ip", nullable = false)
    private String ip;


    public Instance(Long instanceId) {
        this.setId(instanceId);
    }


    Instance findInstance() {

        return getBeanByClass(InstanceRepository.class).findByAppEnvClusterAndDataCenterAndIp(this.appEnvCluster,this.dataCenter, this.ip);
    }

    public void setDataCenter(String dataCenter){
        this.dataCenter = Strings.isNullOrEmpty(dataCenter) ? "" : dataCenter;
    }
}
