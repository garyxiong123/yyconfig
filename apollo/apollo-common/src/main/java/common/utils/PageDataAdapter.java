package common.utils;

import com.youyu.common.api.PageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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

    public static PageData toPageData(Pageable pageable, List rows, int totalCount) {
        PageData pageData = new PageData(pageable.getPageNumber() + 1, pageable.getPageSize());
        pageData.setRows(rows);
        pageData.setTotalCount(totalCount);

        int totalPage = totalCount / pageable.getPageSize() + 1;
        pageData.setTotalPage(totalPage);
        return pageData;
    }

}
