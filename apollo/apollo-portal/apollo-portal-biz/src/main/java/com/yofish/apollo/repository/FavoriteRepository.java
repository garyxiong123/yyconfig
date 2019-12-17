package com.yofish.apollo.repository;

import com.yofish.apollo.domain.App;
import com.yofish.apollo.domain.Favorites;
import com.yofish.gary.biz.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * created by zhangyingbin on 2019/12/13 0013 下午 2:39
 * description:
 */
@Component
public interface FavoriteRepository extends JpaRepository<Favorites,Long> {

    Favorites findByUserAndApp(User user, App app);
}
