package com.yofish.apollo.domain;

import com.yofish.apollo.repository.ReleaseRepository;
import com.yofish.apollo.service.ReleaseHistoryService;
import com.yofish.apollo.service.ReleaseService;
import com.yofish.gary.dao.entity.BaseEntity;
import common.constants.ReleaseOperation;
import common.exception.BadRequestException;
import common.exception.NotFoundException;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

import static com.yofish.gary.bean.StrategyNumBean.getBeanInstance;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:49
 */

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name="releases")
public class Release extends BaseEntity {


    @Column(nullable = false)
    private String releaseKey;

    @Column(nullable = false)
    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    private AppEnvClusterNamespace appEnvClusterNamespace;

    @Column(name = "Configurations", nullable = false)
    @Lob
    private String configurations;

    private boolean isEmergencyPublish;



    private boolean abandoned;



    public Release publish(){
//        release = releaseRepository.save(release);

        return null;
    }


}
