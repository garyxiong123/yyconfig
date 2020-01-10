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


import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.Commit;
import com.yofish.apollo.repository.CommitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author rache
 * @date 2019-12-17
 */
@Service
public class CommitService {
    @Autowired
    private CommitRepository commitRepository;

    public void saveCommit(AppEnvClusterNamespace appEnvClusterNamespace,String changeSets){
        Commit commit = new Commit();
        commit.setAppEnvClusterNamespace(appEnvClusterNamespace);
        commit.setChangeSets(changeSets);
        commitRepository.save(commit);
    }
    public List<Commit> find(Long namespaceId){
        AppEnvClusterNamespace appEnvClusterNamespace=new AppEnvClusterNamespace();
        appEnvClusterNamespace.setId(namespaceId);
       return commitRepository.findAllByAppEnvClusterNamespace(appEnvClusterNamespace);
    }
    public List<Commit> find(AppEnvClusterNamespace appEnvClusterNamespace){
       return commitRepository.findAllByAppEnvClusterNamespace(appEnvClusterNamespace);
    }
}
