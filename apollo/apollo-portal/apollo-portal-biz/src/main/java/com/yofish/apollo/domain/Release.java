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
