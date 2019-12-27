package com.yofish.apollo.domain;

import com.yofish.apollo.util.ReleaseMessageKeyGenerator;
import com.yofish.gary.dao.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
public class ReleaseMessage extends BaseEntity {

    @Column(name = "Message", nullable = false)
    private String message;

    public ReleaseMessage(AppEnvClusterNamespace namespace) {
        this.message = ReleaseMessageKeyGenerator.generate(namespace);
    }
}

