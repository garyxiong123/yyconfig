package com.yofish.apollo.domain;

import com.google.common.collect.Maps;
import com.yofish.apollo.repository.AppEnvClusterNamespace4BranchRepository;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.service.AppNamespaceService;
import common.constants.ReleaseOperation;
import common.constants.ReleaseOperationContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.Map;

import static com.yofish.gary.bean.StrategyNumBean.getBeanInstance;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/15 下午10:14
 */
@Data
@Entity
@DiscriminatorValue("main")
public class AppEnvClusterNamespace4Main extends AppEnvClusterNamespace {

    public AppEnvClusterNamespace4Main(AppEnvCluster appEnvCluster, AppNamespace appNamespace) {
        super(appEnvCluster, appNamespace);
    }

    public AppEnvClusterNamespace4Main() {

    }

    public AppEnvClusterNamespace4Branch getBranchNamespace() {
        return getBeanInstance(AppEnvClusterNamespace4BranchRepository.class).findByParentId(this.getId());
    }

    public boolean hasBranchNamespace() {
        return getBeanInstance(AppEnvClusterNamespace4BranchRepository.class).findByParentId(this.getId()) != null;
    }


}
