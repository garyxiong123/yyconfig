package com.yofish.apollo.domain;

import com.yofish.gary.dao.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class GrayReleaseRule extends BaseEntity {

    @OneToOne
    private Release4Branch release;


    @Column(nullable = false)
    private String branchName;

    private String rules;


    @Column(nullable = false)
    private int branchStatus;

}
