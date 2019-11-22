package com.yofish.gary.biz.repository;

import com.yofish.gary.biz.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 2018/2/5.
 *
 * @author zlf
 * @since 1.0
 */
public interface RoleRepository extends JpaRepository<Role, Long> {


    Role findRoleByRoleName(String roleName);
}
