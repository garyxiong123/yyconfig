package com.yofish.apollo.domain;

import com.google.common.base.MoreObjects;
import com.yofish.gary.dao.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class InstanceConfig extends BaseEntity {

    @ManyToOne
    private Instance instance;

    @Column(name = "ReleaseKey", nullable = false)
    private String releaseKey;

    private LocalDateTime releaseDeliveryTime;
}
