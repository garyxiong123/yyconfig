/*
 *    Copyright 2018-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.yofish.gary.biz.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.google.common.collect.Sets;
import com.yofish.gary.api.ShiroSimpleHashStrategy;
import com.yofish.gary.api.dto.req.UserAddReqDTO;
import com.yofish.gary.api.dto.req.UserEditReqDTO;
import com.yofish.gary.api.dto.req.UserModifyPasswordReqDTO;
import com.yofish.gary.api.enums.RoleTypeEnum;
import com.yofish.gary.biz.repository.RoleRepository;
import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;
import org.springframework.util.ObjectUtils;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.yofish.gary.api.enums.UpmsResultCode.USER_STATUS_INVALID;
import static com.yofish.gary.api.enums.UpmsResultCode.WRONG_PASSWORD;
import static com.yofish.gary.api.enums.UserStatusEnum.INVALID;
import static com.yofish.gary.api.enums.UserStatusEnum.VALID;
import static com.yofish.gary.bean.StrategyNumBean.getBeanInstance;
import static com.yofish.gary.utils.BizExceptionUtil.exception;
import static com.yofish.gary.utils.BizExceptionUtil.exception2MatchingExpression;
import static com.yofish.gary.utils.StringUtil.eq;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 用户
 * <p>
 * 、一个类继承结构一个表的策略
 * 表中有一列被当作“discriminator列”，即使用该列来识别某行数据属于某个指定的子类实例
 * @DiscriminatorValue(子类实体辨别字段列值)
 */


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@DiscriminatorColumn(name = "DISCRIMINATOR", discriminatorType = DiscriminatorType.STRING, length = 30)
public class User extends BaseEntity {


    /**
     * 用户名
     */
    @Column(name = "username")
    private String username;

    /**
     * 密码
     */
    @Column(name = "password")
    private String password;

    /**
     * 真实姓名
     */
    @Column(name = "real_name")
    private String realName;

    /**
     * 性别(0:男 1:女 2:其他)
     */
    @Column(name = "sex")
    private Integer sex;

    /**
     * 手机号
     */
    @Column(name = "phone")
    private String phone;

    /**
     * 邮箱
     */
    @Column(name = "email")
    private String email;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 状态(0:有效 1:冻结)
     */
    @Column(name = "status")
    private String status;


    private HashMap extInfo;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE})
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles = new HashSet<>();

    public User(Long id) {
        super(id);
    }

    public User(String username) {
        this();
        this.username = username;
    }


    public User(String username, String password) {
        this(username);
        this.password = password;
    }

    public User(UserAddReqDTO userAddReqDTO, String hashAlgorithmName) {
        this(userAddReqDTO.getUsername(), getBeanInstance(ShiroSimpleHashStrategy.class, hashAlgorithmName).signature(userAddReqDTO.getPassword()));
        this.realName = userAddReqDTO.getRealName();
        this.sex = userAddReqDTO.getSex();
        this.phone = userAddReqDTO.getPhone();
        this.email = userAddReqDTO.getEmail();
        this.status = defaultIfBlank(userAddReqDTO.getStatus(), VALID.getCode());
        this.remark = userAddReqDTO.getRemark();
        this.department = ObjectUtils.isEmpty(userAddReqDTO.getDepartmentId())?null:new Department(userAddReqDTO.getDepartmentId());
        List<Long> roleIds = userAddReqDTO.getRoleIds();
        setUserRoles(roleIds);
    }

    public User(UserEditReqDTO userEditReqDTO, String hashAlgorithmName) {
        this();
        setId(userEditReqDTO.getId());
        this.realName = userEditReqDTO.getRealName();
        this.sex = userEditReqDTO.getSex();
        this.phone = userEditReqDTO.getPhone();
        this.email = userEditReqDTO.getEmail();
        this.status = defaultIfBlank(userEditReqDTO.getStatus(), VALID.getCode());
        this.remark = userEditReqDTO.getRemark();
        if (isNoneBlank(userEditReqDTO.getPassword())) {
            this.password = getBeanInstance(ShiroSimpleHashStrategy.class, hashAlgorithmName).signature(userEditReqDTO.getPassword());
        }
        this.department = ObjectUtils.isEmpty(userEditReqDTO.getDepartmentId())?null:new Department(userEditReqDTO.getDepartmentId());
        List<Long> roleIds = userEditReqDTO.getRoleIds();
        setUserRoles(roleIds);
    }

    private void setUserRoles(List<Long> roleIds) {
        if(!isEmpty(roleIds)){
            List<Role> roles = getBeanInstance(RoleRepository.class).findAllById(roleIds);
            this.setRoles(Sets.newHashSet(roles));
        }
    }

    @Builder
    public User(Long id, String username, String password, String realName, Integer sex, String phone, String email, String remark, String status, HashMap extInfo, Department department, Set<Role> roles) {
        super(id);
        this.username = username;
        this.password = password;
        this.realName = realName;
        this.sex = sex;
        this.phone = phone;
        this.email = email;
        this.remark = remark;
        this.status = status;
        this.extInfo = extInfo;
        this.department = department;
        this.roles = roles;
    }

    public void checkStatus() {
        exception2MatchingExpression(eq(status, INVALID.getCode()), USER_STATUS_INVALID);

    }

    public boolean isAdmin() {
        RoleRepository roleRepository = getBeanInstance(RoleRepository.class);
        Role roleByRoleName = roleRepository.findRoleByRoleName(RoleTypeEnum.ADMIN.name());
        if (ObjectUtils.isEmpty(roleByRoleName) || ObjectUtils.isEmpty(roleByRoleName.getUsers())) {
            return false;
        } else {
            return roleByRoleName.getUsers().contains(this);
        }
    }

    /**
     * 修改密码
     *
     * @param userModifyPasswordReqDTO
     * @param hashAlgorithmName
     */
    public void modifyPassword(UserModifyPasswordReqDTO userModifyPasswordReqDTO, String hashAlgorithmName) {
        checkPassword(userModifyPasswordReqDTO.getOriginPassword(), hashAlgorithmName);
        this.password = signature(userModifyPasswordReqDTO.getPassword(), hashAlgorithmName);
    }

    /**
     * 检查用户输入的密码是否合法
     *
     * @param originPassword
     * @param hashAlgorithmName
     */
    private void checkPassword(String originPassword, String hashAlgorithmName) {
        String password = signature(originPassword, hashAlgorithmName);
        if (!eq(this.password, password)) {
            exception(WRONG_PASSWORD);
        }
    }

    /**
     * 散列原始密码
     *
     * @param password
     * @param hashAlgorithmName
     * @return
     */
    private String signature(String password, String hashAlgorithmName) {
        return getBeanInstance(ShiroSimpleHashStrategy.class, hashAlgorithmName).signature(password);
    }

}
