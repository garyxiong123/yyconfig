package com.yofish.apollo.domain;

import com.yofish.apollo.repository.ReleaseRepository;
import com.yofish.apollo.service.ReleaseHistoryService;
import com.yofish.apollo.service.ReleaseService;
import com.yofish.apollo.strategy.PublishStrategy4Main;
import com.yofish.apollo.util.ReleaseKeyGenerator;
import com.youyu.common.enums.BaseResultCode;
import com.youyu.common.exception.BizException;
import common.constants.ReleaseOperation;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;
import java.util.Map;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass;
import static com.yofish.gary.bean.StrategyNumBean.getBeanInstance;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/17 上午10:54
 */
@Data
@Entity
@DiscriminatorValue("Release4Main")
public class Release4Main extends Release {

    @Builder
    public Release4Main(AppEnvClusterNamespace namespace, String name, String comment, Map<String, String> configurations, boolean isEmergencyPublish, String releaseKey) {
        super(namespace, name, comment, configurations, isEmergencyPublish, releaseKey);
    }

    @Override
    public Release publish() {

        Release release = getBeanByClass(PublishStrategy4Main.class).publish(this);

        return release;
    }


    public Release4Branch getBranchRelease() {
        return null;
    }

    @Transactional
    public Release rollback() {

        if (isAbandoned()) {
            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, "release is not active");
        }
        AppEnvClusterNamespace4Main namepsace = (AppEnvClusterNamespace4Main) this.getAppEnvClusterNamespace();

        PageRequest page = new PageRequest(0, 2);
        List<Release> twoLatestActiveReleases = namepsace.findLatestActiveReleases(page);
        if (twoLatestActiveReleases == null || twoLatestActiveReleases.size() < 2) {
//            throw new BizException(BaseResultCode.REQUEST_PARAMS_WRONG, String.format("Can't rollback appNamespace(appId=%s, clusterName=%s, namespaceName=%s) because there is only one active release",
//                    appId,
//                    clusterName,
//                    namespaceName));
        }

        setAbandoned(true);

        getBeanInstance(ReleaseRepository.class).save(this);

        getBeanInstance(ReleaseHistoryService.class).createReleaseHistory(this, twoLatestActiveReleases.get(1).getId(), ReleaseOperation.ROLLBACK, null);

        //publish child appNamespace if appNamespace has child 灰度回滚
        Release4Branch release4Branch = this.findLastBranchRelease();
        if (release4Branch != null) {
            release4Branch.rollback(this, twoLatestActiveReleases);
        }
        return this;
    }

    private Release4Branch findLastBranchRelease() {

        return null;
    }


}

