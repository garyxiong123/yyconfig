package com.yofish.apollo;

import com.yofish.apollo.domain.*;
import com.yofish.apollo.dto.CreateItemReq;
import com.yofish.apollo.model.vo.NamespaceIdentifier;
import framework.apollo.core.enums.Env;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/17 下午5:28
 */
public class DomainCreate {

    public static final String branchName = "2017-1222-branch";

    public static AppEnvClusterNamespace4Main createNamespace4Main() {
        AppEnvClusterNamespace4Main appEnvClusterNamespace4Main = createAppEnvClusterNamespace4Main();
        return appEnvClusterNamespace4Main;
    }

    public static CreateItemReq createItemReq() {
        return CreateItemReq.builder().key("dbName").lineNum(1).value("payment").build();
    }

    public static AppEnvClusterNamespace4Main createAppEnvClusterNamespace4Main() {

        AppEnvClusterNamespace4Main namespace4Main = new AppEnvClusterNamespace4Main();
        App app = createApp();
        namespace4Main.setAppEnvCluster(createAppEnvCluster(app));
        namespace4Main.setAppNamespace(createAppNamespace(app));

        return namespace4Main;
    }

    public static App createApp() {
        return App.builder().appCode("middleground13").name("中台支付").build();
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

    public static List<NamespaceIdentifier> createNamespaceIdentifier(){
        List<NamespaceIdentifier> nas=new ArrayList<>();
        NamespaceIdentifier namespaceIdentifier1=new NamespaceIdentifier();
        NamespaceIdentifier namespaceIdentifier2=new NamespaceIdentifier();
        NamespaceIdentifier namespaceIdentifier3=new NamespaceIdentifier();
        namespaceIdentifier1.setAppEnvClusterId(1L);
        namespaceIdentifier2.setAppEnvClusterId(2L);
        namespaceIdentifier3.setAppEnvClusterId(3L);
        nas.add(namespaceIdentifier1);
        nas.add(namespaceIdentifier2);
        nas.add(namespaceIdentifier3);
        return nas;
    }

    public static List<Item> createItemList(){
        List<Item> items=new ArrayList<>();
        Item item1=Item.builder().appEnvClusterNamespace(DomainCreate.createNamespace4Main())
                .key("spring.datasource.url").value("123123").build();
        Item item2=Item.builder().appEnvClusterNamespace(DomainCreate.createNamespace4Main())
                .key("message.a.value").value("h12313").build();
        items.add(item1);
        items.add(item2);
        return items;
    }

}
