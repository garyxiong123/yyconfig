package com.gary.apollo.test.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: xiongchengwei
 * @Date: 2020/1/15 下午5:26
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Permission4Menu extends Permission {


    private String menu_url;

    private String menu_name;

}
