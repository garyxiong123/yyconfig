package com.gary.apollo.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xiongchengwei
 * @Date: 2020/1/15 下午5:28
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Permission4Data extends Permission {



    private String table_name;
}
