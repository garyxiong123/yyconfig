package com.yofish.apollo.util;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;

/**
 * @author rache
 * @date 2019-12-19
 */
@Data
public class PageQuery<T> {

    @ApiModelProperty(value = "请求页码")
    protected int pageNo = 1;

    @ApiModelProperty(value = "请求每页大小")
    protected int pageSize = 10;

    private T data;

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo <= 0 ? 1 : pageNo;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 0 ? 10 : pageSize;
    }
}
