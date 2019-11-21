package com.yofish.apollo.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:50
 */

@NoArgsConstructor
@Data
@Entity
@DiscriminatorValue("Namespace4Public")
public class Namespace4Public extends Namespace {
}
