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
package com.yofish.apollo.enums;

import com.yofish.apollo.domain.*;
import com.yofish.apollo.model.AppNamespaceModel;
import com.yofish.apollo.service.AppService;
import org.springframework.util.ObjectUtils;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass;

/**
 * @author WangSongJun
 * @date 2019-12-10
 */
public enum AppNamespaceType {
    /**
     * 公共的
     */
    Public {
        @Override
        public void doBuildAppNamespace(AppNamespace appNamespace, AppNamespaceModel appNamespaceModel) {
            appNamespace.setName(this.appendDepartmentCodePrefix(appNamespaceModel.getAppId(), appNamespaceModel.getName()));
            appNamespace.setOpenNamespaceType(ObjectUtils.isEmpty(appNamespaceModel.getOpenNamespaceTypeId()) ? null : new OpenNamespaceType(appNamespaceModel.getOpenNamespaceTypeId()));
        }
    },

    /**
     * 保护的，需授权
     */
    Protect {
        @Override
        public void doBuildAppNamespace(AppNamespace appNamespace, AppNamespaceModel appNamespaceModel) {
            appNamespace.setName(this.appendDepartmentCodePrefix(appNamespaceModel.getAppId(), appNamespaceModel.getName()));
            appNamespace.setOpenNamespaceType(ObjectUtils.isEmpty(appNamespaceModel.getOpenNamespaceTypeId()) ? null : new OpenNamespaceType(appNamespaceModel.getOpenNamespaceTypeId()));
            appNamespace.setAuthorizedApp(appNamespaceModel.getAuthorizedApp());
            appNamespace.setName(this.appendDepartmentCodePrefix(appNamespaceModel.getAppId(), appNamespaceModel.getName()));
        }

    },

    /**
     * 项目私有的
     */
    Private {
        @Override
        public void doBuildAppNamespace(AppNamespace appNamespace, AppNamespaceModel appNamespaceModel) {

        }
    };


//    public static <T extends AppNamespace> AppNamespaceType getNamespaceTypeByInstance(T t) {
//        if (t instanceof AppNamespace4Private) {
//            return Private;
//        } else if (t instanceof AppNamespace4Protect) {
//            return Protect;
//        } else if (t instanceof AppNamespace4Public) {
//            return Public;
//        } else {
//            return null;
//        }
//    }


    public abstract void doBuildAppNamespace(AppNamespace appNamespace, AppNamespaceModel appNamespaceModel);


    /**
     * 设置部门名称作为前缀
     *
     * @param appId
     * @param namespaceName
     * @return
     */
    public String appendDepartmentCodePrefix(long appId, String namespaceName) {
        String departmentCodePrefix = getBeanByClass(AppService.class).getAppById(appId).getDepartment().getCode() + ".";
        if (!namespaceName.startsWith(departmentCodePrefix)) {
            return departmentCodePrefix + namespaceName;
        } else {
            return namespaceName;
        }
    }

}
