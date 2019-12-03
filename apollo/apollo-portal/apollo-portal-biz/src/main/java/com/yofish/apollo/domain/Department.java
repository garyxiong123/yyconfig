package com.yofish.apollo.domain;

import com.yofish.gary.dao.entity.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import java.time.LocalDateTime;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/12 上午10:49
 */

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
public class Department extends BaseEntity {


    private String name;

    @Builder
    public Department(Long id, String createAuthor, LocalDateTime createTime, String updateAuthor, LocalDateTime updateTime, String name) {
        super(id, createAuthor, createTime, updateAuthor, updateTime);
        this.name = name;
    }
}
