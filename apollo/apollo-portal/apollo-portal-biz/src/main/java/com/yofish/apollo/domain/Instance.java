package com.yofish.apollo.domain;

import com.yofish.apollo.dto.CreateItemReq;
import com.yofish.apollo.dto.UpdateItemReq;
import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:51
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
public class Instance extends BaseEntity {

        private String ip;
}
