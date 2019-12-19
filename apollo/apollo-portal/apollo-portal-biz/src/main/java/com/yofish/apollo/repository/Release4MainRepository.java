package com.yofish.apollo.repository;

import com.yofish.apollo.domain.Release;
import com.yofish.apollo.domain.Release4Main;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
@Component
public interface Release4MainRepository extends JpaRepository<Release4Main, Long> {


}
