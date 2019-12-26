package com.yofish.gary.biz.domain;

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

    private String code;

    private String name;

    private String comment;

    public Department(Long id) {
        super(id);
    }

    @Builder
    public Department(Long id, String createAuthor, LocalDateTime createTime, String updateAuthor, LocalDateTime updateTime, String code, String name, String comment) {
        super(id, createAuthor, createTime, updateAuthor, updateTime);
        this.code = code;
        this.name = name;
        this.comment = comment;
    }
}
