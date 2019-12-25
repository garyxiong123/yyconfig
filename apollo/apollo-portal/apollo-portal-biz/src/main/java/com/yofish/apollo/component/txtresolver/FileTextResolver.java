package com.yofish.apollo.component.txtresolver;

import com.yofish.apollo.bo.ItemChangeSets;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.Item;
import framework.apollo.core.ConfigConsts;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.springframework.util.ObjectUtils.isEmpty;

@Component("fileTextResolver")
public class FileTextResolver implements ConfigTextResolver {


  @Override
  public ItemChangeSets resolve(long namespaceId, String configText, List<Item> baseItems) {
    ItemChangeSets changeSets = new ItemChangeSets();
    if (isEmpty(configText)) {
      return changeSets;
    }
    if (isEmpty(baseItems)) {
      changeSets.addCreateItem(createItem(namespaceId, 0, configText));
    } else {
      Item beforeItem = baseItems.get(0);
      if (!configText.equals(beforeItem.getValue())) {//update
        changeSets.addUpdateItem(createItem(namespaceId, beforeItem.getId(), configText));
      }
    }

    return changeSets;
  }

  private Item createItem(long namespaceId, long itemId, String value) {
    Item item = new Item();
    item.setId(itemId);
    AppEnvClusterNamespace appEnvClusterNamespace=new AppEnvClusterNamespace();
    appEnvClusterNamespace.setId(namespaceId);
    item.setAppEnvClusterNamespace(appEnvClusterNamespace);
    item.setValue(value);
    item.setLineNum(1);
    item.setKey(ConfigConsts.CONFIG_FILE_CONTENT_KEY);
    return item;
  }
}
