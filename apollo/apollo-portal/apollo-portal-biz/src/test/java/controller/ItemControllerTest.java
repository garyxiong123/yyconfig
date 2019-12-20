/*
package controller;

import com.yofish.apollo.controller.ItemController;
import com.yofish.apollo.domain.Item;
import com.yofish.apollo.dto.CreateItemReq;
import com.yofish.apollo.dto.ItemReq;
import com.yofish.apollo.dto.ModifyItemsByTextsReq;
import com.yofish.apollo.dto.UpdateItemReq;
import com.yofish.apollo.model.NamespaceTextModel;
import com.yofish.apollo.model.model.NamespaceSyncModel;
import com.yofish.apollo.model.vo.ItemDiffs;
import com.yofish.apollo.model.vo.NamespaceIdentifier;
import com.youyu.common.api.Result;
import common.exception.BadRequestException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static common.utils.RequestPrecondition.checkModel;

*/
/**
 * @author rache
 * @date 2019-12-20
 *//*

public class ItemControllerTest extends AbstractControllerTest{
    @Autowired
    private ItemController itemController;

    @Test
    public void modifyItemsByText(@RequestBody ModifyItemsByTextsReq model) {

        checkModel(model != null);
        itemService.updateConfigItemByText(model);
    }


    @Test
    public Result<Item> createItem(@RequestBody CreateItemReq req) {
        Item item= itemService.createItem(req);
        return Result.ok(item);
    }


    @Test
    public Result updateItem(@RequestBody UpdateItemReq req) {
        itemService.updateItem(req);
        return Result.ok();
    }



    @Test
    public void deleteItem(@RequestBody ItemReq req) {
        if (req.getClusterNamespaceId() <= 0) {
            throw new BadRequestException("item id invalid");
        }
        itemService.deleteItem(req);
    }


    @Test
    public Result<List<Item>> findItems(@RequestBody ItemReq req) {
        List<Item> items = itemService.findItemsWithoutOrdered(req);
        return Result.ok(items);
    }



    @Test
    public Result updateEnv(@PathVariable String appId, @PathVariable String namespaceName,
                            @RequestBody NamespaceSyncModel model) {
        itemService.syncItems(model.getSyncToNamespaces(), model.getSyncItems());
        return Result.ok();
    }


    @Test
    public List<ItemDiffs> diff(@RequestBody NamespaceSyncModel model) {

        List<ItemDiffs> itemDiffs = itemService.compare(model.getSyncToNamespaces(), model.getSyncItems());

        for (ItemDiffs diff : itemDiffs) {
            NamespaceIdentifier namespace = diff.getNamespace();
            if (namespace == null) {
                continue;
            }
        }

        return itemDiffs;
    }

    @Test
    public void syntaxCheckText() {
        NamespaceTextModel namespaceTextModel=new NamespaceTextModel();
        itemController.syntaxCheckText(namespaceTextModel);


    }

}
*/
