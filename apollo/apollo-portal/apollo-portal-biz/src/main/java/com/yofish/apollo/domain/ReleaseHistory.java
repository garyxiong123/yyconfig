package com.yofish.apollo.domain;

import com.yofish.gary.dao.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * @author Jason Song(song_s@ctrip.com)
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
public class ReleaseHistory extends BaseEntity {

    @ManyToOne(cascade = CascadeType.DETACH)
    private Release release;

    @ManyToOne(cascade = CascadeType.DETACH)
    private Release previousRelease;

    @Column(name = "Operation")
    private int operation;

    @Column(name = "OperationContext", nullable = false)
    private String operationContext;

    private Long namespaceId;


}
