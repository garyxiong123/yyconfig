package controller;

import com.alibaba.fastjson.JSONObject;
import com.yofish.apollo.controller.ItemController;
import com.yofish.apollo.domain.Item;
import com.yofish.apollo.dto.CreateItemReq;
import com.yofish.apollo.dto.ItemReq;
import com.yofish.apollo.dto.ModifyItemsByTextsReq;
import com.yofish.apollo.dto.UpdateItemReq;
import com.yofish.apollo.model.NamespaceTextModel;
import com.yofish.apollo.model.model.NamespaceSyncModel;
import com.youyu.common.api.Result;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


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
        ModifyItemsByTextsReq modifyItemsByTextsReq=new ModifyItemsByTextsReq();
        itemController.modifyItemsByText(modifyItemsByTextsReq);
    }


    @Test
    public void createItem() {
       CreateItemReq req=new CreateItemReq();
       req.setAppEnvClusterNamespaceId(1L);
       req.setKey("spring.datasource.url2");
       req.setValue("jdbc:mysql://192.168.1.95:3306/ops-upms?useUnicode=true&amp;characterEncoding=UTF-8");
       req.setComment("数据库连接地址2");
       req.setLineNum(2);
       itemController.createItem(req);
    }


    @Test
    public void updateItem() {
        UpdateItemReq req=new UpdateItemReq();
        req.setItemId(9L);
        req.setKey("key");
        req.setValue("value");
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
    public void updateEnv() {
        NamespaceSyncModel namespaceSyncModel=new NamespaceSyncModel();
        itemController.updateEnv(namespaceSyncModel);
    }


    @Test
    public void diff() {

        NamespaceSyncModel model=new  NamespaceSyncModel();
        itemController.diff(model);

    }

    @Test
    public void syntaxCheckText() {
        NamespaceTextModel namespaceTextModel=new NamespaceTextModel();
        itemController.syntaxCheckText(namespaceTextModel);


    }

}
