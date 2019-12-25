package com.yofish.apollo.component.txtresolver;

import com.yofish.apollo.bo.ItemChangeSets;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.Item;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import common.utils.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * normal property file resolver.
 * update comment and blank item implement by create new item and delete old item.
 * update normal key/value item implement by update.
 */
@Component("propertyResolver")
public class PropertyResolver implements ConfigTextResolver {

    private static final String KV_SEPARATOR = "=";
    private static final String ITEM_SEPARATOR = "\n";

    @Override
    public ItemChangeSets resolve(long appEnvClusterNamespaceId, String configText, List<Item> baseItems) {

     Map<Integer, Item> oldLineNumMapItem = BeanUtils.mapByKey("lineNum", baseItems);
     Map<String, Item> oldKeyMapItem = BeanUtils.mapByKey("key", baseItems);
     //remove comment and blank item map.
     oldKeyMapItem.remove("");

     String[] newItems = configText.split(ITEM_SEPARATOR);

      if (isHasRepeatKey(newItems)) {
        throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "config text has repeat key please check.");
      }

     ItemChangeSets changeSets = new ItemChangeSets();
     Map<Integer, String> newLineNumMapItem = new HashMap<Integer, String>();//use for delete blank and comment item
     int lineCounter = 1;
     for (String newItem : newItems) {
      newItem = newItem.trim();
      newLineNumMapItem.put(lineCounter, newItem);
      Item oldItemByLine = oldLineNumMapItem.get(lineCounter);

      //comment item
      if (isCommentItem(newItem)) {

        handleCommentLine(appEnvClusterNamespaceId, oldItemByLine, newItem, lineCounter, changeSets);

        //blank item
      } else if (isBlankItem(newItem)) {

        handleBlankLine(appEnvClusterNamespaceId, oldItemByLine, lineCounter, changeSets);

        //normal item
      } else {
        handleNormalLine(appEnvClusterNamespaceId, oldKeyMapItem, newItem, lineCounter, changeSets);
      }

      lineCounter++;
    }

    deleteCommentAndBlankItem(oldLineNumMapItem, newLineNumMapItem, changeSets);
    deleteNormalKVItem(oldKeyMapItem, changeSets);

    return changeSets;
    }

    private boolean isHasRepeatKey(String[] newItems) {
        Set<String> keys = new HashSet<>();
        int lineCounter = 1;
        int keyCount = 0;
        for (String item : newItems) {
            if (!isCommentItem(item) && !isBlankItem(item)) {
                keyCount++;
                String[] kv = parseKeyValueFromItem(item);
                if (kv != null) {
                    keys.add(kv[0]);
                } else {
//                    throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "line:" + lineCounter + " key value must separate by '='");
                }
            }
            lineCounter++;
        }

        return keyCount > keys.size();
    }

    private String[] parseKeyValueFromItem(String item) {
        int kvSeparator = item.indexOf(KV_SEPARATOR);
        if (kvSeparator == -1) {
            return null;
        }

        String[] kv = new String[2];
        kv[0] = item.substring(0, kvSeparator).trim();
        kv[1] = item.substring(kvSeparator + 1, item.length()).trim();
        return kv;
    }

    private void handleCommentLine(Long namespaceId, Item oldItemByLine, String newItem, int lineCounter, ItemChangeSets changeSets) {
        String oldComment = oldItemByLine == null ? "" : oldItemByLine.getComment();
        //create comment. implement update comment by delete old comment and create new comment
        if (!(isCommentItem(oldItemByLine) && newItem.equals(oldComment))) {
            changeSets.addCreateItem(buildCommentItem(0L, namespaceId, newItem, lineCounter));
        }
    }

    private void handleBlankLine(Long namespaceId, Item oldItem, int lineCounter, ItemChangeSets changeSets) {
        if (!isBlankItem(oldItem)) {
            changeSets.addCreateItem(buildBlankItem(0L, namespaceId, lineCounter));
        }
    }

    private void handleNormalLine(Long namespaceId, Map<String, Item> keyMapOldItem, String newItem,
                                  int lineCounter, ItemChangeSets changeSets) {

        String[] kv = parseKeyValueFromItem(newItem);

        if (kv == null) {
//            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "line:" + lineCounter + " key value must separate by '='");
        }

        String newKey = kv[0];
        String newValue = kv[1].replace("\\n", "\n"); //handle user input \n

        Item oldItem = keyMapOldItem.get(newKey);

        if (oldItem == null) {//new item
            changeSets.addCreateItem(buildNormalItem(0l, namespaceId, newKey, newValue, "", lineCounter));
        } else if (!newValue.equals(oldItem.getValue()) || lineCounter != oldItem.getLineNum()) {//update item
            changeSets.addUpdateItem(
                    buildNormalItem(oldItem.getId(), namespaceId, newKey, newValue, oldItem.getComment(),
                            lineCounter));
        }
        keyMapOldItem.remove(newKey);
    }

    private boolean isCommentItem(Item item) {
        return item != null && "".equals(item.getKey())
                && (item.getComment().startsWith("#") || item.getComment().startsWith("!"));
    }

    private boolean isCommentItem(String line) {
        return line != null && (line.startsWith("#") || line.startsWith("!"));
    }

    private boolean isBlankItem(Item item) {
        return item != null && "".equals(item.getKey()) && "".equals(item.getComment());
    }

    private boolean isBlankItem(String line) {
        return "".equals(line);
    }

    private void deleteNormalKVItem(Map<String, Item> baseKeyMapItem, ItemChangeSets changeSets) {
        //surplus item is to be deleted
        for (Map.Entry<String, Item> entry : baseKeyMapItem.entrySet()) {
            changeSets.addDeleteItem(entry.getValue());
        }
    }

    private void deleteCommentAndBlankItem(Map<Integer, Item> oldLineNumMapItem,
                                           Map<Integer, String> newLineNumMapItem,
                                           ItemChangeSets changeSets) {

        for (Map.Entry<Integer, Item> entry : oldLineNumMapItem.entrySet()) {
            int lineNum = entry.getKey();
            Item oldItem = entry.getValue();
            String newItem = newLineNumMapItem.get(lineNum);

            //1. old is blank by now is not
            //2.old is comment by now is not exist or modified
            if ((isBlankItem(oldItem) && !isBlankItem(newItem))
                    || isCommentItem(oldItem) && (newItem == null || !newItem.equals(oldItem.getComment()))) {
                changeSets.addDeleteItem(oldItem);
            }
        }
    }

    private Item buildCommentItem(Long id, Long appEnvClusterNamespaceId, String comment, int lineNum) {
        return buildNormalItem(id, appEnvClusterNamespaceId, "", "", comment, lineNum);
    }

    private Item buildBlankItem(Long id, Long appEnvClusterNamespaceId, int lineNum) {
        return buildNormalItem(id, appEnvClusterNamespaceId, "", "", "", lineNum);
    }

    private Item buildNormalItem(Long id, Long appEnvClusterNamespaceId, String key, String value, String comment, int lineNum) {
        AppEnvClusterNamespace appEnvClusterNamespace=new AppEnvClusterNamespace();
        appEnvClusterNamespace.setId(appEnvClusterNamespaceId);
        Item item = new Item(key, value, comment,appEnvClusterNamespace, lineNum);
        item.setId(id);
        return item;
    }
}
