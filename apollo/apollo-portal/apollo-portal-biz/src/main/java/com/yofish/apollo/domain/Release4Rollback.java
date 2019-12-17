package com.yofish.apollo.domain;

import com.yofish.apollo.repository.ReleaseRepository;
import com.yofish.apollo.service.ReleaseHistoryService;
import com.yofish.apollo.service.ReleaseService;
import common.constants.ReleaseOperation;
import common.exception.BadRequestException;
import common.exception.NotFoundException;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

import static com.yofish.gary.bean.StrategyNumBean.getBeanInstance;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/17 上午10:54
 */

@Data
@Entity
@DiscriminatorValue("Release4Rollback")
public class Release4Rollback extends Release {


    @Override
    public Release publish() {
        rollback();
        return null;

    }

    @Transactional
    public Release rollback() {
        if (this == null) {
            throw new NotFoundException("release not found");
        }
        if (isAbandoned()) {
            throw new BadRequestException("release is not active");
        }
        AppEnvClusterNamespace4Main namepsace = (AppEnvClusterNamespace4Main) this.getAppEnvClusterNamespace();

        PageRequest page = new PageRequest(0, 2);
        List<Release> twoLatestActiveReleases = namepsace.findLatestActiveReleases(page);
        if (twoLatestActiveReleases == null || twoLatestActiveReleases.size() < 2) {
//            throw new BadRequestException(String.format("Can't rollback appNamespace(appId=%s, clusterName=%s, namespaceName=%s) because there is only one active release",
//                    appId,
//                    clusterName,
//                    namespaceName));
        }

        setAbandoned(true);

        getBeanInstance(ReleaseRepository.class).save(this);

        getBeanInstance(ReleaseHistoryService.class).createReleaseHistory(this, twoLatestActiveReleases.get(1).getId(), ReleaseOperation.ROLLBACK, null);

        //publish child appNamespace if appNamespace has child 灰度回滚

        if (namepsace.hasBranchNamespace()){
            getBeanInstance(ReleaseService.class).rollbackChildNamespace(this, twoLatestActiveReleases);
        }
        return this;
    }
}
