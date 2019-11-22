package com.yofish.gary.biz.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

/**
 * @Author: xiongchengwei
 * @Date: 2019/10/20 下午2:31
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Embeddable
public class AddressVO {

    private String street;

    private String road;

    private String city;

    private String district;

}
