


package com.yofish.apollo.repository;


import com.yofish.apollo.domain.Favorites;
import com.yofish.apollo.domain.Item;
import com.yofish.gary.biz.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 *
 * @author gary
 * @version $Id: ItemRepository.java,
 *  v0.1 2019-11-12 19:01:03 gary Exp $$
 */
public interface FacoritesRepository extends JpaRepository<Favorites, Long> {

    List<Favorites> findFavoritesByUser(User user);

}
