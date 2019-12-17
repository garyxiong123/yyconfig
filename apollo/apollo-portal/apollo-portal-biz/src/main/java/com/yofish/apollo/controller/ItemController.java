package com.yofish.apollo.controller;

//import com.yofish.apollo.model.model.NamespaceTextModel;
import com.yofish.apollo.domain.Item;
import com.yofish.apollo.dto.CreateItemReq;
import com.yofish.apollo.dto.ItemReq;
import com.yofish.apollo.dto.ModifyItemsByTextsReq;
import com.yofish.apollo.dto.UpdateItemReq;
import com.yofish.apollo.model.NamespaceTextModel;
import com.yofish.apollo.service.ItemService;
import com.youyu.common.api.Result;
import common.exception.BadRequestException;
import framework.apollo.core.enums.ConfigFileFormat;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

import static common.utils.RequestPrecondition.checkModel;


@RestController
@RequestMapping("item")
public class ItemController {

  @Autowired
  private ItemService itemService;


  @RequestMapping(value = "/modifyItemsByTexts", method = RequestMethod.POST, consumes = {
      "application/json"})
  public void modifyItemsByText(@RequestBody ModifyItemsByTextsReq model) {

    checkModel(model != null);
    itemService.updateConfigItemByText(model);
  }


  @RequestMapping(value = "/createItem", method = RequestMethod.POST)
  public Result<Item> createItem(@RequestBody CreateItemReq req) {
    Item item= itemService.createItem(req);
    return Result.ok(item);
  }


  @RequestMapping(value = "/updateItem", method = RequestMethod.PUT)
  public Result updateItem(@RequestBody UpdateItemReq req) {
     itemService.updateItem(req);
    return Result.ok();
  }



  @RequestMapping(value = "deleteItem", method = RequestMethod.DELETE)
  public void deleteItem(@RequestBody ItemReq req) {
    if (req.getClusterNamespaceId() <= 0) {
      throw new BadRequestException("item id invalid");
    }
    itemService.deleteItem(req);
  }


  @RequestMapping(value = "/findItems", method = RequestMethod.GET)
  public Result<List<Item>> findItems(@RequestBody ItemReq req) {
    List<Item> items = itemService.findItems(req);
    return Result.ok(items);
  }

//todo 配置同步

  /*@RequestMapping(value = "/apps/{appId}/namespaces/{namespaceName}/items", method = RequestMethod.PUT, consumes = {
      "application/json"})
  public ResponseEntity<Void> update(@PathVariable String appId, @PathVariable String namespaceName,
                                     @RequestBody NamespaceSyncModel model) {
    checkModel(Objects.nonNull(model) && !model.isInvalid());
    boolean hasPermission = permissionValidator.hasModifyNamespacePermission(appId, namespaceName);
    Env envNoPermission = null;
    // if uses has ModifyNamespace permission then he has permission
    if (!hasPermission) {
      // else check if user has every env's ModifyNamespace permission
      hasPermission = true;
      for (NamespaceIdentifier namespaceIdentifier : model.getSyncToNamespaces()) {
        // once user has not one of the env's ModifyNamespace permission, then break the loop
        hasPermission &= permissionValidator.hasModifyNamespacePermission(namespaceIdentifier.getAppId(), namespaceIdentifier.getNamespaceName(), namespaceIdentifier.getEnv().toString());
        if (!hasPermission) {
          envNoPermission = namespaceIdentifier.getEnv();
          break;
        }
      }
    }
    if (hasPermission) {
      configService.syncItems(model.getSyncToNamespaces(), model.getSyncItems());
      return ResponseEntity.status(HttpStatus.OK).build();
    }
    else {
      throw new AccessDeniedException(String.format("您没有修改环境%s的权限", envNoPermission));
    }
  }
*/

  @PostMapping(value = "syntax-check")
  public ResponseEntity<Void> syntaxCheckText(@RequestBody NamespaceTextModel model) {

    doSyntaxCheck(model);

    return ResponseEntity.ok().build();
  }

  private void doSyntaxCheck(NamespaceTextModel model) {
    if (StringUtils.isBlank(model.getConfigText())) {
      return;
    }

    // only support yaml syntax check
    if (model.getFormat() != ConfigFileFormat.YAML && model.getFormat() != ConfigFileFormat.YML) {
      return;
    }

    // use YamlPropertiesFactoryBean to check the yaml syntax
    YamlPropertiesFactoryBean yamlPropertiesFactoryBean = new YamlPropertiesFactoryBean();
    yamlPropertiesFactoryBean.setResources(new ByteArrayResource(model.getConfigText().getBytes()));
    // this call converts yaml to properties and will throw exception if the conversion fails
    yamlPropertiesFactoryBean.getObject();
  }


}
