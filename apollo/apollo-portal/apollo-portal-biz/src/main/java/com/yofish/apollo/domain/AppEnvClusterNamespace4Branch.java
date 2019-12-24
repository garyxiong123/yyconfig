package com.yofish.apollo.domain;

import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Map;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/15 下午10:14
 */
@Data
@Entity
@DiscriminatorValue("branch")
public class AppEnvClusterNamespace4Branch extends AppEnvClusterNamespace {

    private Long parentId;

    @ManyToOne(cascade = CascadeType.DETACH)
    private GrayReleaseRule grayReleaseRule;

    private String branchName;

    public AppEnvClusterNamespace4Branch() {
    }

    public AppEnvClusterNamespace4Branch(Long parentId) {
        this.parentId = parentId;
    }


    public AppEnvClusterNamespace4Branch(AppEnvCluster appEnvCluster, AppNamespace appNamespace) {
        super(appEnvCluster, appNamespace);
    }


    public AppEnvClusterNamespace4Main getMainNamespace() {
        AppEnvClusterNamespace4Main appEnvClusterNamespace = (AppEnvClusterNamespace4Main) getBeanByClass(AppEnvClusterNamespaceRepository.class).findById(parentId).orElseGet(null);
        return appEnvClusterNamespace;
    }

}
