


package com.yofish.apollo.repository;


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


//    List<Item> findItemsByClusterNamespace_IdNamespaceAndClusterNamespace_NameNamespace(Long namespaceId, String name);
}