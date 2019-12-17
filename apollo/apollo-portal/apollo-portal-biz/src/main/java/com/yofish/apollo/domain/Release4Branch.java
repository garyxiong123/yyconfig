package com.yofish.apollo.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * @Author: xiongchengwei
 * @Date: 2019/12/17 上午10:54
 */
@Data
@Entity
@DiscriminatorValue("Release4Branch")
public class Release4Branch extends Release {

    @Column(name = "Comment", nullable = false)
    private String comment;


    @Override
    public Release publish() {

        return super.publish();
    }


}
