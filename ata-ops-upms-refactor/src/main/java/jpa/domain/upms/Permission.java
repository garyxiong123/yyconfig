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
package jpa.domain.upms;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author pqq
 * @version v1.0
 * @date 2019年6月27日 10:00:00
 * @work 权限
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "upms_permission")
@Entity
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 权限编码
     */
    @Column(name = "code")
    private String code;

    /**
     * 权限名称
     */
    @Column(name = "permission_name")
    private String permissionName;

    /**
     * 权限url
     */
    @Column(name = "url")
    private String url;

    /**
     * 权限类型(0:菜单 1:按钮 2:iframe 3:其他)
     * 注:后续扩展
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 图标
     */
    @Column(name = "icon")
    private String icon;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 父级权限
     */
    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 排序
     */
    @Column(name = "rank")
    private Integer rank;

    /**
     * iframe地址
     */
    @Column(name = "iframe_url")
    private String iframeUrl;

    /**
     * iframe数据
     */
    @Column(name = "iframe_json")
    private String iframeJson;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();


    public String getName() {
        return this.permissionName;
    }

    public String getPath() {
        return this.url;
    }
}
