package com.yofish.apollo.dto;

import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

/**
 * @author rache
 * @date 2019-12-24
 */
@Data
public class ItemDto {
    private Long id;
    private Long appEnvClusterNamespaceId;
    private String key;
    private String value;
    private String comment;
    private Integer lineNum;
    private String createAuthor;
    private LocalDateTime createTime;
    private String updateAuthor;
    private LocalDateTime updateTime;
}
