package com.yofish.apollo.dto;

import com.yofish.apollo.domain.Item;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: xiongchengwei
 * @Date: 2019/11/18 下午3:04
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateItemReq {
    private String key;
    private String value;
    private String comment;
    private List<Long> appEnvClusterNamespaceIds;
    private Integer lineNum;
}
