package com.yofish.apollo.repository;

import com.yofish.apollo.domain.OpenNamespaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author WangSongJun
 * @date 2019-12-24
 */
@Repository
public interface OpenNamespaceTypeRepository extends JpaRepository<OpenNamespaceType, Long> {
}
