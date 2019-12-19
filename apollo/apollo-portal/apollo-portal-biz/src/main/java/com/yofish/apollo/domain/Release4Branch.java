package com.yofish.apollo.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Map;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/17 上午10:54
 */
@Data
@Entity
@DiscriminatorValue("Release4Branch")
public class Release4Branch extends Release {

    @Column(name = "Comment", nullable = false)
    private String comment;

    public Release4Branch(AppEnvClusterNamespace namespace, String name, String comment, Map<String, String> configurations, boolean isEmergencyPublish) {
        super(namespace,  name,  comment, configurations, isEmergencyPublish);
        this.setComment(comment);

    }


    @Override
    public Release publish() {

        return super.publish();
    }


    public Release4Main getMainRelease() {
        return null;
    }
}
