package common.utils;

import com.youyu.common.api.PageData;
import org.springframework.data.domain.Page;

/**
 * PageDataAdapter 转PageData
 *
 * @author WangSongJun
 * @date 2019-12-03
 */
public class PageDataAdapter {
    public static PageData toPageData(Page page) {
        PageData pageData = new PageData(page.getNumber() + 1, page.getSize());
        pageData.setRows(page.getContent());
        pageData.setTotalCount(page.getTotalElements());
        pageData.setTotalPage(page.getTotalPages());
        return pageData;
    }

}
