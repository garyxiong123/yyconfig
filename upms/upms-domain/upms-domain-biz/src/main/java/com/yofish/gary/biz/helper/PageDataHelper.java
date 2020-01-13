/*
 *    Copyright 2019-2020 the original author or authors.
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
package com.yofish.gary.biz.helper;

import com.youyu.common.api.PageData;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author WangSongJun
 * @date 2019-12-17
 */
public class PageDataHelper {
    public static PageData toPageData(Page page, List data) {
        PageData pageData = new PageData(page.getNumber() + 1, page.getSize());
        pageData.setTotalCount(page.getTotalElements());
        pageData.setTotalPage(page.getTotalPages());
        pageData.setRows(data);
        return pageData;
    }

    public static PageData toPageData(Page page) {
        PageData pageData = new PageData(page.getNumber() + 1, page.getSize());
        pageData.setTotalCount(page.getTotalElements());
        pageData.setTotalPage(page.getTotalPages());
        pageData.setRows(page.getContent());
        return pageData;
    }
}
