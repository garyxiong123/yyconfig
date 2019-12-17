package com.yofish.apollo.domain;

import com.yofish.gary.biz.domain.User;
import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:49
 */

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Favorites extends BaseEntity {


    @ManyToOne(cascade = CascadeType.DETACH)
    private User user;

    @ManyToOne(cascade = CascadeType.DETACH)
    private App app;


}
