package com.yofish.apollo.domain;

import com.yofish.gary.biz.domain.User;
import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
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
public class App extends BaseEntity {

    private String appCode;
    private String name;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Department department;

    @ManyToOne(cascade = CascadeType.DETACH)
    private User appOwner;

    @OneToMany(cascade = {CascadeType.DETACH})
    private Set<User> appAdmins;


    @Builder
    public App(Long id, String createAuthor, LocalDateTime createTime, String updateAuthor, LocalDateTime updateTime, String appCode, String name, Department department, Set<User> appAdmins, User appOwner) {
        super(id, createAuthor, createTime, updateAuthor, updateTime);
        this.appCode = appCode;
        this.name = name;
        this.department = department;
        this.appAdmins = appAdmins;
        this.appOwner = appOwner;
    }
}
