package com.yofish.apollo.domain;

import com.yofish.apollo.enums.ServerConfigKey;
import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;

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
    private String key;

    @Column(name = "Value", nullable = false)
    private String value;

    @Column(name = "Comment", nullable = false)
    private String comment;

}