package com.yofish.apollo.service;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.AppEnvCluster;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author WangSongJun
 * @date 2019-12-20
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {com.yofish.apollo.JpaApplication.class})
public class AppEnvClusterServiceTest {
    @Autowired
    private AppEnvClusterService appEnvClusterService;

    @Test
    public void findClusters() {
        String env = "dev";
        long appId = 35l;
        List<AppEnvCluster> clusters = appEnvClusterService.findClusters(env, appId);
    }

    @Test
    public void createAppEnvCluster() {
        String clusterName = "cluster-test";
        String env = "dev";
        App app = new App(35L);
        AppEnvCluster appEnvCluster = new AppEnvCluster(clusterName, env, app);

        appEnvCluster = appEnvClusterService.createAppEnvCluster(appEnvCluster);

    }

    @Test
    public void getAppEnvCluster() {
    }

    @Test
    public void deleteAppEnvCluster() {
    }

    @Test
    public void isClusterNameUnique() {
    }

    @Test
    public void createClusterInEachActiveEnv() {
    }
}