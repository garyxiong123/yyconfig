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
package com.yofish.apollo.service;

import com.google.gson.Gson;
import com.yofish.apollo.domain.Release;
import com.yofish.apollo.domain.ReleaseHistory;
import com.yofish.apollo.api.dto.ReleaseHistoryDTO;
import com.yofish.apollo.repository.ReleaseHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@Service
public class ReleaseHistoryService {
    private Gson gson = new Gson();

    @Autowired
    private ReleaseHistoryRepository releaseHistoryRepository;
//  @Autowired
//  private AuditService auditService;


    public Page<ReleaseHistory> findReleaseHistoriesByNamespace(Release release, Pageable pageable) {

        return releaseHistoryRepository.findReleaseHistoriesByRelease(release, pageable);
    }

    public Page<ReleaseHistory> findByReleaseIdAndOperation(Release release, int operation, Pageable page) {
        return releaseHistoryRepository.findReleaseHistorysByReleaseAndOperationOrderByIdDesc(release, operation, page);
    }

    public Page<ReleaseHistory> findByPreviousReleaseIdAndOperation(long previousReleaseId, int operation, Pageable page) {
        return releaseHistoryRepository.findByPreviousReleaseIdAndOperationOrderByIdDesc(previousReleaseId, operation, page);
    }

    @Transactional
    public ReleaseHistory createReleaseHistory(Long namespaceId, Release release, Release previousRelease, int operation, Map<String, Object> operationContext) {

        ReleaseHistory releaseHistory = new ReleaseHistory();
        releaseHistory.setNamespaceId(namespaceId);
        releaseHistory.setRelease(release);
        releaseHistory.setPreviousRelease(previousRelease);
        releaseHistory.setOperation(operation);
        if (operationContext == null) {
            releaseHistory.setOperationContext("{}"); //default empty object
        } else {
            releaseHistory.setOperationContext(gson.toJson(operationContext));
        }

        releaseHistoryRepository.save(releaseHistory);

//    auditService.audit(ReleaseHistory.class.getSimpleName(), releaseHistory.getId(),
//                       Audit.OP.INSERT, releaseHistory.getDataChangeCreatedBy());

        return releaseHistory;
    }

    @Transactional
    public int batchDelete(String appId, String clusterName, String namespaceName, String operator) {
        return releaseHistoryRepository.batchDelete(appId, clusterName, namespaceName, operator);
    }


    public Page<ReleaseHistoryDTO> findNamespaceReleaseHistory(Long namespaceId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReleaseHistory> releaseHistories = releaseHistoryRepository.findByNamespaceIdOrderByIdDesc(namespaceId, pageable);

        return transform2Dto(releaseHistories);
    }

    private Page<ReleaseHistoryDTO> transform2Dto(Page<ReleaseHistory> releaseHistories) {

        return null;
    }
}
