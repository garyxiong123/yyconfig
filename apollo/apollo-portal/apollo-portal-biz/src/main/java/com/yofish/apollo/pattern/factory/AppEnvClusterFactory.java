package com.yofish.apollo.pattern.factory;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.AppEnvCluster;
import com.yofish.apollo.model.dto.Req.CreateClusterReqDTO;
import com.yofish.apollo.repository.AppEnvClusterRepository;
import com.yofish.apollo.service.AppEnvClusterNamespaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明
 * @Date: 2020/7/27 下午4:16
 */
@Component
public class AppEnvClusterFactory {

    @Autowired
    private AppEnvClusterRepository appEnvClusterRepository;
    @Autowired
    private AppEnvClusterNamespaceService appEnvClusterNamespaceService;

    public List<AppEnvCluster> createAppEnvClusters(CreateClusterReqDTO createClusterReqDTO) {
        List<AppEnvCluster> appEnvClusterList = new ArrayList<>();
        Arrays.stream(createClusterReqDTO.getEnvs().split(",")).forEach(env -> {
            AppEnvCluster appEnvCluster = AppEnvCluster.builder().app(new App(createClusterReqDTO.getAppId())).env(env).name(createClusterReqDTO.getClusterName()).build();
            createAppEnvClusters(appEnvCluster);
            appEnvClusterList.add(appEnvCluster);
        });

        return appEnvClusterList;
    }

    private void createAppEnvClusters(AppEnvCluster appEnvCluster) {
        appEnvClusterRepository.save(appEnvCluster);
        // create linked namespace
        this.appEnvClusterNamespaceService.instanceOfAppNamespaces(appEnvCluster);
    }
}
