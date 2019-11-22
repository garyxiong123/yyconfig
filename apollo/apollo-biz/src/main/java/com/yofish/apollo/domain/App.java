package com.yofish.apollo.domain;

import com.yofish.gary.biz.domain.User;
import com.yofish.gary.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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
public class App extends BaseEntity {

    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    private Department department;

    @OneToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private Set<User> appAdmins;

    @ManyToOne(cascade = CascadeType.ALL)
    private User appOwner;

}
