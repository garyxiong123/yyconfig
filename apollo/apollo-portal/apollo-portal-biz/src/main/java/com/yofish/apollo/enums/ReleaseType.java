package com.yofish.apollo.enums;


import com.yofish.apollo.domain.Release;
import com.yofish.apollo.pattern.strategy.publish.PublishStrategy4Branch;
import com.yofish.apollo.pattern.strategy.publish.PublishStrategy4Main;

import static com.yofish.gary.bean.StrategyNumBean.getBeanByClass;

/**
 * @Author: xiongchengwei
 * @version:
 * @Description: 类的主要职责说明
 * @Date: 2020/7/27 下午1:14
 */
public enum ReleaseType {
    MAIN {
        @Override
        public void publish(Release release) {
            getBeanByClass(PublishStrategy4Main.class).publish(release);

        }
    }, BRANCH {
        @Override
        public void publish(Release release) {
            getBeanByClass(PublishStrategy4Branch.class).publish(release);

        }
    };


    public abstract void publish(Release release);
}
