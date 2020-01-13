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

import com.yofish.apollo.DomainCreate;
import com.yofish.apollo.controller.NamespaceBranchController;
import com.yofish.apollo.controller.ReleaseController;
import com.yofish.apollo.domain.AppEnvClusterNamespace4Main;
import com.yofish.apollo.model.model.NamespaceReleaseModel;
import com.yofish.apollo.repository.AppEnvClusterNamespaceRepository;
import com.yofish.apollo.repository.ReleaseRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.yofish.apollo.DomainCreate.createAppEnvClusterNamespace4Main;


public class NamespaceBranchControllerTest extends AbstractControllerTest {

    @Autowired
    ReleaseRepository releaseRepository;
    @Autowired
    NamespaceBranchController namespaceBranchController;
    @Autowired
    private AppEnvClusterNamespaceRepository namespaceRepository;
    public AppEnvClusterNamespace4Main namespace4Main;

    @Before
    public void setUp() {
        namespace4Main = createAppEnvClusterNamespace4Main();
    }

    @Test
    public void testCreateBranch() {
        namespaceRepository.save(namespace4Main);
        namespaceBranchController.createBranch(namespace4Main.getId(), DomainCreate.branchName);
    }

    @Test
    public void testCreateBranchException4hasBranch() {
        namespaceRepository.save(namespace4Main);
        namespaceBranchController.createBranch(namespace4Main.getId(), DomainCreate.branchName);

        namespaceBranchController.createBranch(namespace4Main.getId(), DomainCreate.branchName);
    }

}
