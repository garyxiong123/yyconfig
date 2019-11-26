package com.yofish.apollo.domain;

import com.yofish.gary.entity.BaseEntity;
import lombok.*;

import javax.persistence.Entity;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:50
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class Env extends BaseEntity {

    private String name;

}
