package com.yofish.apollo;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.AppEnvCluster;
import com.yofish.apollo.domain.AppEnvClusterNamespace4Main;
import com.yofish.apollo.domain.AppNamespace;
import framework.apollo.core.enums.Env;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/17 下午5:28
 */
public class DomainCreate {

    public static AppEnvClusterNamespace4Main createNamespace4Main() {
        AppEnvClusterNamespace4Main appEnvClusterNamespace4Main = createAppEnvClusterNamespace4Main();
        return appEnvClusterNamespace4Main;
    }

    public static AppEnvClusterNamespace4Main createAppEnvClusterNamespace4Main() {

        AppEnvClusterNamespace4Main namespace4Main = new AppEnvClusterNamespace4Main();
        App app = createApp();
        namespace4Main.setAppEnvCluster(createAppEnvCluster(app));
        namespace4Main.setAppNamespace(createAppNamespace(app));

        return namespace4Main;
    }

    public static App createApp() {
        return App.builder().appCode("middleground").name("中台支付").build();
    }

    public static AppEnvCluster createAppEnvCluster(App app) {

        return AppEnvCluster.builder().app(app).name("default集群").env(Env.DEV.name()).build();
    }


    public static AppNamespace createAppNamespace(App app) {
        AppNamespace appNamespace = new AppNamespace();
        appNamespace.setApp(app);
        appNamespace.setName("DB-config");
        return appNamespace;
    }

}
