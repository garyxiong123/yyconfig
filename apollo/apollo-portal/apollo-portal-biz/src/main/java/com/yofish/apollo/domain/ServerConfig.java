package com.yofish.apollo.domain;

import com.yofish.apollo.config.ServerConfigKey;
import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/5 下午3:11
 * @see ServerConfigKey
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class ServerConfig extends BaseEntity {
    @Column(name = "`Key`", nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private ServerConfigKey key;

    @Column(name = "Value", nullable = false)
    private String value;

    @Column(name = "Comment", nullable = false)
    private String comment;

}