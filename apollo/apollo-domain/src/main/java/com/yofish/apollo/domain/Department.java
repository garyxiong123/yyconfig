package com.yofish.apollo.domain;

import com.yofish.gary.entity.BaseEntity;
import lombok.*;

import javax.persistence.Entity;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:49
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class Department extends BaseEntity {


    private String name;
}
