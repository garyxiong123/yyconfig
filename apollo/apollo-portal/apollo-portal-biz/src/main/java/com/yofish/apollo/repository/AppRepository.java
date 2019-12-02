package com.yofish.apollo.repository;

import com.yofish.apollo.domain.App;
import com.yofish.gary.biz.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
@Component
public interface AppRepository extends JpaRepository<App, Long> {


    App findByAppId(String appId);


}
