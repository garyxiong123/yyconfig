/*
 *    Copyright 2019-2020 the original author or authors.
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
package com.yofish.apollo.component.constant;

public interface PermissionType {

  /**
   * system level permission
   */
  String CREATE_APPLICATION = "CreateApplication";
  String MANAGE_APP_MASTER = "ManageAppMaster";

  /**
   * APP level permission
   */

  String CREATE_NAMESPACE = "CreateNamespace";

  String CREATE_CLUSTER = "CreateCluster";

  /**
   * 分配用户权限的权限
   */
  String ASSIGN_ROLE = "AssignRole";

  /**
   * appNamespace level permission
   */

  String MODIFY_NAMESPACE = "ModifyNamespace";

  String RELEASE_NAMESPACE = "ReleaseNamespace";


    /**
     * @author Jason Song(song_s@ctrip.com)
     */
    interface Topics {
        String APOLLO_RELEASE_TOPIC = "apollo-release";
    }
}
