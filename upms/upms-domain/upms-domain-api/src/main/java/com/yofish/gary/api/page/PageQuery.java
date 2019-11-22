/*
 *    Copyright 2018-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.yofish.gary.api.page;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author pqq
 * @version v1.0
 * @date 2018年12月5日 10:00:00
 * @work 分页基类
 */
@Getter
public class PageQuery implements Serializable {

    private static final long serialVersionUID = 559624532997511828L;

    @ApiModelProperty(value = "请求页码")
    protected int pageNo = 1;

    @ApiModelProperty(value = "请求每页大小")
    protected int pageSize = 10;

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo <= 0 ? 1 : pageNo;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize <= 0 ? 10 : pageSize;
    }
}
