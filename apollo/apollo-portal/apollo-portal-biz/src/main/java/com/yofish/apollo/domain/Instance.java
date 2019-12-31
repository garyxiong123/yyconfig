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
