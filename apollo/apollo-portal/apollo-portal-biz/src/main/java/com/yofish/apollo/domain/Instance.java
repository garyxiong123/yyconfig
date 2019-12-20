package com.yofish.apollo.domain;

import com.google.common.base.MoreObjects;
import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Instance extends BaseEntity {
    @ManyToOne
    private AppEnvClusterNamespace appEnvClusterNamespace;

    @Column(name = "DataCenter", nullable = false)
    private String dataCenter;

    @Column(name = "Ip", nullable = false)
    private String ip;


}
