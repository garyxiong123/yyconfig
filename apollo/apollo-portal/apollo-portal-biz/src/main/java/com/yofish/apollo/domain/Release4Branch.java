package com.yofish.apollo.domain;

import javax.persistence.Column;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/17 上午10:54
 */

public class Release4Branch extends Release {

    @Column(name = "Comment", nullable = false)
    private String comment;


    @Override
    public Release publish() {

        return super.publish();
    }


}
