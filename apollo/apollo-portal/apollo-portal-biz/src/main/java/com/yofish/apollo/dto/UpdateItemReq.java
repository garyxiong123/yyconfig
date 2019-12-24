package com.yofish.apollo.dto;

import lombok.Data;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/18 下午3:06
 */
@Data
public class UpdateItemReq {
    private Long itemId;

    private String key;

    private String value;

    private String comment;

    private int lineNum;

}
