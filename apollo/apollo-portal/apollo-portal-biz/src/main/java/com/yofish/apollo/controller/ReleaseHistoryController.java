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
package com.yofish.apollo.controller;


//import com.yofish.apollo.api.model.bo.ReleaseHistoryBO;
import com.yofish.apollo.component.PermissionValidator;
import com.yofish.apollo.api.dto.ReleaseHistoryDTO;
import com.yofish.apollo.service.ReleaseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReleaseHistoryController {
    @Autowired
    private ReleaseHistoryService releaseHistoryService;
    @Autowired
    private PermissionValidator permissionValidator;

    @RequestMapping(value = "/namespaceId/{namespaceId}/releases/histories", method = RequestMethod.GET)
    public Page<ReleaseHistoryDTO> findReleaseHistoriesByNamespace(@PathVariable Long namespaceId,
                                                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                                                   @RequestParam(value = "size", defaultValue = "10") int size) {

//        if (permissionValidator.shouldHideConfigToCurrentUser(namespaceId)) {
//            return Collections.emptyList();
//        }

        return releaseHistoryService.findNamespaceReleaseHistory(namespaceId, page, size);
    }

}
