package com.yofish.apollo.domain;

import com.yofish.apollo.repository.AppRepository;
import com.yofish.gary.biz.domain.Department;
import com.yofish.gary.biz.domain.User;
import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

import static com.yofish.gary.bean.StrategyNumBean.getBeanInstance;

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

    @Column(unique = true)
    private String appCode;
    private String name;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Department department;

    @ManyToOne(cascade = CascadeType.DETACH)
    private User appOwner;

    @ManyToMany(cascade = {CascadeType.DETACH})
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

    public App(Long id) {
        super(id);
    }
    public static App creatApp(String appCode) {
        AppRepository appRepository = getBeanInstance(AppRepository.class);
        return appRepository.findByAppCode(appCode);
    }
    public AppEnvCluster createAppEnvCluster(String co){
        return new AppEnvCluster();
    }

}
