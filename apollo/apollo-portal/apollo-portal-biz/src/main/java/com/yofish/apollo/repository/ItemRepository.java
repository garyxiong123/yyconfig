


package com.yofish.apollo.repository;


import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *
 * @author gary
 * @version $Id: ItemRepository.java,
 *  v0.1 2019-11-12 19:01:03 gary Exp $$
 */
public interface ItemRepository  extends JpaRepository<Item, Long> {


   List<Item> findAllByAppEnvClusterNamespace(AppEnvClusterNamespace appEnvClusterNamespace);
//   Item findFirstByClusterNamespaceOrderByLineNumDesc(AppEnvClusterNamespace clusterNamespace);
   Item findFirstByAppEnvClusterNamespaceOrderByLineNumDesc(AppEnvClusterNamespace appEnvClusterNamespace);
   Item findItemByAppEnvClusterNamespaceAndKey(AppEnvClusterNamespace appEnvClusterNamespace,String key);
}
