package com.yofish.apollo.repository;

import com.yofish.apollo.domain.App;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
@Component
public interface AppRepository extends JpaRepository<App, Long>, JpaSpecificationExecutor<App> {

    /**
     * findByAppCode
     *
     * @param appCode
     * @return
     */
    App findByAppCode(String appCode);



    /**
     * findByAppCodeContainingOrNameContaining
     *
     * @param appId
     * @param name
     * @param pageable
     * @return
     */
    Page<App> findByAppCodeContainingOrNameContaining(String appId, String name, Pageable pageable);

    /**
     * findAllByAppCodeContainingOrNameContaining
     *
     * @param appId
     * @param name
     * @return
     */
    List<App> findAllByAppCodeContainingOrNameContaining(String appId, String name);

}
