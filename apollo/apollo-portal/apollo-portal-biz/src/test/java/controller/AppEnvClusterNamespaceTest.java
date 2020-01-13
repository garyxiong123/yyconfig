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
package controller;

import com.yofish.apollo.controller.AppNamespaceController;
import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.dto.NamespaceEnvTree;
import com.yofish.apollo.dto.NamespaceListReq;
import com.yofish.apollo.dto.NamespaceListResp;
import com.yofish.apollo.model.bo.NamespaceVO;
import com.youyu.common.api.Result;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author rache
 * @date 2019-12-25
 */

public class AppEnvClusterNamespaceTest extends AbstractControllerTest{
    @Autowired
    private AppNamespaceController appNamespaceController;

    @Test
    public void list(){
        NamespaceListReq req=new NamespaceListReq();
        req.setAppCode("payment");
        req.setNamespace("application");
        Result<List<NamespaceEnvTree>> listResult= appNamespaceController.namespaceList(req);
        System.out.println(1);
    }

    @Test
    public void findNamespaces(){
        Result<List<NamespaceVO>> nameSpace= appNamespaceController.findNamespaces("middleground1","test","default");
        System.out.println(1);
    }
}
