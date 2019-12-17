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
@DiscriminatorValue("Release4Main")
public class Release4Main extends Release {

    @Column(name = "Comment", nullable = false)
    private String comment;


    @Override
    public Release publish() {

        Map<String, String> operateNamespaceItems = null;
        this.getAppEnvClusterNamespace().publish(null, comment, null, isEmergencyPublish());
        return this;
    }

}
