package com.yofish.apollo.service;

import com.yofish.apollo.domain.AppEnvClusterNamespace;
import com.yofish.apollo.domain.Commit;
import com.yofish.apollo.repository.CommitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
