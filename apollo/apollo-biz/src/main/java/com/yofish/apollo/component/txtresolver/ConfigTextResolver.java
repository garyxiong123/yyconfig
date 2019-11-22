package com.yofish.apollo.component.txtresolver;


import com.yofish.apollo.bo.ItemChangeSets;
import com.yofish.apollo.domain.Item;

import java.util.List;

/**
 * users can modify config in text mode.so need resolve text.
 */
public interface ConfigTextResolver {

    ItemChangeSets resolve(long namespaceId, String configText, List<Item> baseItems);

}
