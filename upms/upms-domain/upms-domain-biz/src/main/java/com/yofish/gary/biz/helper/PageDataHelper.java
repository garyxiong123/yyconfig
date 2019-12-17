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
