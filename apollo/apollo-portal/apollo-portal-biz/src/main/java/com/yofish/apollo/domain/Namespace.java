package com.yofish.apollo.domain;

import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:50
 */

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Builder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING, length = 30)
public class Namespace extends BaseEntity {

    private Long appId;

    private String env;

    private String clusterName;

    private String namespaceName;


}
