/*
 *    Copyright 2019-2020 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package controller;

import com.yofish.apollo.DomainCreate;
import com.yofish.apollo.model.bo.ItemChangeSetDto;
import com.yofish.apollo.controller.ItemController;
import com.yofish.apollo.domain.Item;
import com.yofish.apollo.api.dto.*;
import com.yofish.apollo.model.NamespaceSyncModel;
import com.yofish.apollo.model.NamespaceTextModel;
import com.youyu.common.api.Result;
import com.yofish.yyconfig.common.framework.apollo.core.enums.ConfigFileFormat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author rache
 * @date 2019-12-20
 */

public class ItemControllerTest extends AbstractControllerTest{
    @Autowired
    private ItemController itemController;

    @Test
    public void modifyItemsByText() {
        ModifyItemsByTextsReq modifyItemsByTextsReq=createModifyItemsByTextsReq();
        itemController.modifyItemsByText(modifyItemsByTextsReq);
    }


    @Test
    public void createItem() {
       CreateItemReq req=new CreateItemReq();
       req.setAppEnvClusterNamespaceIds(new ArrayList<>(Arrays.asList(38L)));
       req.setKey("23");
       req.setValue("gagag");
       req.setComment("数据库连接地址2");
       itemController.createItem(req);
    }


    @Test
    public void updateItem() {
        UpdateItemReq req=new UpdateItemReq();
        req.setItemId(67L);
        req.setKey("key");
        req.setValue("value1");
        req.setComment("变化");
        req.setLineNum(12);
        itemController.updateItem(req);

    }



    @Test
    public void deleteItem() {
        ItemReq req =new ItemReq();
        req.setItemId(9L);
        itemController.deleteItem(req);
    }


    @Test
    public void findItems() {
        ItemReq req =new ItemReq();
        req.setClusterNamespaceId(1L);
        Result<List<Item>> listResult= itemController.findItems(req);
        System.out.println(1);
       // System.out.println(JSONObject.toJSONString(listResult.getData()));
    }






    @Test
    public void diff() {

       Result<List<ItemChangeSetDto>>   itemDiffs= itemController.diff(createNamespaceSyncModel());
        System.out.println(1);
    }

    @Test
    public void updateEnv() {
        NamespaceSyncModel namespaceSyncModel=createNamespaceSyncModel();
        itemController.updateEnv(namespaceSyncModel);
    }

    @Test
    public void syntaxCheckText() {
        NamespaceTextModel namespaceTextModel=namespaceTextModel();

        itemController.syntaxCheckText(namespaceTextModel);


    }


    private ModifyItemsByTextsReq createModifyItemsByTextsReq(){
        ModifyItemsByTextsReq req=new ModifyItemsByTextsReq();
        req.setAppEnvClusterNamespaceId(19L);
        req.setConfigText(propertityFromat());
        req.setFormat(ConfigFileFormat.Properties);
       /* req.setConfigText(jsonFromat());
        req.setFormat(ConfigFileFormat.JSON);*/
        return req;
    }

    private NamespaceTextModel namespaceTextModel(){
        NamespaceTextModel namespaceTextModel=new NamespaceTextModel();
        namespaceTextModel.setFormat("properties");
        namespaceTextModel.setConfigText(propertityFromat());
        return namespaceTextModel;
    }

    private NamespaceSyncModel createNamespaceSyncModel(){
        NamespaceSyncModel model=new  NamespaceSyncModel();
        model.setSyncToNamespaces(DomainCreate.createNamespaceIdentifier());
        model.setSyncItems(DomainCreate.createItemList());
        return model;
    }

    private String jsonFromat(){

        String s="[{\"namespace\":{\"appCode\":\"platform-sample-provider\",\"env\":\"DEV\",\"clusterName\":\"shanghai\",\"namespaceName\":\"application\"},\"diffs\":{\"createItems\":[],\"updateItems\":[{\"id\":14983,\"namespaceId\":203,\"key\":\"112\",\"value\":\"1223\",\"lineNum\":1,\"dataChangeCreatedBy\":\"apollo\",\"dataChangeLastModifiedBy\":\"apollo\",\"dataChangeCreatedTime\":\"2019-12-06T15:03:05.000+0800\",\"dataChangeLastModifiedTime\":\"2019-12-06T15:03:05.000+0800\"}],\"deleteItems\":[]}},{\"namespace\":{\"appCode\":\"platform-sample-provider\",\"env\":\"DEV\",\"clusterName\":\"tes88\",\"namespaceName\":\"application\"},\"diffs\":{\"createItems\":[],\"updateItems\":[{\"id\":14989,\"namespaceId\":261,\"key\":\"112\",\"value\":\"1223\",\"lineNum\":1,\"dataChangeCreatedBy\":\"apollo\",\"dataChangeLastModifiedBy\":\"apollo\",\"dataChangeCreatedTime\":\"2019-12-06T15:03:05.000+0800\",\"dataChangeLastModifiedTime\":\"2019-12-06T15:03:05.000+0800\"}],\"deleteItems\":[]}},{\"namespace\":{\"appCode\":\"platform-sample-provider\",\"env\":\"DEV\",\"clusterName\":\"beijing\",\"namespaceName\":\"application\"},\"diffs\":{\"createItems\":[{\"id\":0,\"namespaceId\":531,\"key\":\"112\",\"value\":\"1223\",\"lineNum\":1,\"dataChangeLastModifiedBy\":\"apollo\"}],\"updateItems\":[],\"deleteItems\":[]}},{\"namespace\":{\"appCode\":\"platform-sample-provider\",\"env\":\"TEST\",\"clusterName\":\"default\",\"namespaceName\":\"application\"},\"diffs\":{\"createItems\":[],\"updateItems\":[{\"id\":10449,\"namespaceId\":77,\"key\":\"112\",\"value\":\"1223\",\"lineNum\":5,\"dataChangeCreatedBy\":\"apollo\",\"dataChangeLastModifiedBy\":\"apollo\",\"dataChangeCreatedTime\":\"2019-12-06T15:03:05.000+0800\",\"dataChangeLastModifiedTime\":\"2019-12-06T15:03:05.000+0800\"}],\"deleteItems\":[]}},{\"namespace\":{\"appCode\":\"platform-sample-provider\",\"env\":\"TEST\",\"clusterName\":\"shanghai\",\"namespaceName\":\"application\"},\"diffs\":{\"createItems\":[],\"updateItems\":[{\"id\":10451,\"namespaceId\":190,\"key\":\"112\",\"value\":\"1223\",\"lineNum\":1,\"dataChangeCreatedBy\":\"apollo\",\"dataChangeLastModifiedBy\":\"apollo\",\"dataChangeCreatedTime\":\"2019-12-06T15:03:05.000+0800\",\"dataChangeLastModifiedTime\":\"2019-12-06T15:03:05.000+0800\"}],\"deleteItems\":[]}},{\"namespace\":{\"appCode\":\"platform-sample-provider\",\"env\":\"TEST\",\"clusterName\":\"tes88\",\"namespaceName\":\"application\"},\"diffs\":{\"createItems\":[],\"updateItems\":[{\"id\":10457,\"namespaceId\":246,\"key\":\"112\",\"value\":\"1223\",\"lineNum\":1,\"dataChangeCreatedBy\":\"apollo\",\"dataChangeLastModifiedBy\":\"apollo\",\"dataChangeCreatedTime\":\"2019-12-06T15:03:05.000+0800\",\"dataChangeLastModifiedTime\":\"2019-12-06T15:03:05.000+0800\"}],\"deleteItems\":[]}},{\"namespace\":{\"appCode\":\"platform-sample-provider\",\"env\":\"PRE\",\"clusterName\":\"default\",\"namespaceName\":\"application\"},\"diffs\":{\"createItems\":[],\"updateItems\":[{\"id\":8025,\"namespaceId\":80,\"key\":\"112\",\"value\":\"1223\",\"lineNum\":6,\"dataChangeCreatedBy\":\"apollo\",\"dataChangeLastModifiedBy\":\"apollo\",\"dataChangeCreatedTime\":\"2019-12-06T15:03:06.000+0800\",\"dataChangeLastModifiedTime\":\"2019-12-06T15:03:06.000+0800\"}],\"deleteItems\":[]}},{\"namespace\":{\"appCode\":\"platform-sample-provider\",\"env\":\"PRE\",\"clusterName\":\"shanghai\",\"namespaceName\":\"application\"},\"diffs\":{\"createItems\":[],\"updateItems\":[{\"id\":8027,\"namespaceId\":193,\"key\":\"112\",\"value\":\"1223\",\"lineNum\":1,\"dataChangeCreatedBy\":\"apollo\",\"dataChangeLastModifiedBy\":\"apollo\",\"dataChangeCreatedTime\":\"2019-12-06T15:03:06.000+0800\",\"dataChangeLastModifiedTime\":\"2019-12-06T15:03:06.000+0800\"}],\"deleteItems\":[]}},{\"namespace\":{\"appCode\":\"platform-sample-provider\",\"env\":\"PRE\",\"clusterName\":\"tes88\",\"namespaceName\":\"application\"},\"diffs\":{\"createItems\":[],\"updateItems\":[{\"id\":8033,\"namespaceId\":249,\"key\":\"112\",\"value\":\"1223\",\"lineNum\":1,\"dataChangeCreatedBy\":\"apollo\",\"dataChangeLastModifiedBy\":\"apollo\",\"dataChangeCreatedTime\":\"2019-12-06T15:03:06.000+0800\",\"dataChangeLastModifiedTime\":\"2019-12-06T15:03:06.000+0800\"}],\"deleteItems\":[]}}]";
        return s;
    }
    private String propertityFromat(){
        String s="spring.datasource.url = jdbc:mysql://test.lb.gs.youyuwo.com:13306/message?serverTimezone=Hongkong&useSSL=false&characterEncoding=utf-8\n" +
                "spring.datasource.username = root\n" +
                "spring.datasource.password = 123.com\n" +
                "spring.datasource.driver-class-name = com.mysql.jdbc.Driver\n" +
                "server.session.timeout = 3600\n" +
                "security.client.ips = 116.226.184.189,58.246.4.210\n" +
                "mapper.mappers = com.caiyi.financial.nirvana.sms.base.mapper.BaseMapper\n" +
                "redis.host = 192.168.1.171:6393,192.168.1.172:6393,192.168.1.172:6394,192.168.1.173:6434,192.168.1.173:6435,192.168.1.171:6394\n" +
                "redis.maxTotal = 100\n" +
                "redis.maxIdle = 100\n" +
                "redis.maxWaitMillis = 6000\n" +
                "redis.timeout = 10000\n" +
                "caiyi.company.accounts = tiyuquzou,liaodaotiyu\n" +
                "finance.company.accounts = youyujizhang,youyudaike";

        String b="dbName = payment11\npassword = 123456\nuserName-toPublish = garyxiong-toPublish\n666 = 5666\n\n\n122 = 2333\n23 = 333444\ndd = dd";
        return b;
    }

}
