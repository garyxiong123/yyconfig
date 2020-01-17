package com.yofish.apollo.model.vo;

import com.yofish.gary.biz.domain.Department;
import com.yofish.gary.biz.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * @author WangSongJun
 * @date 2020-01-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppInfo {

    private Long id;

    private String appCode;

    private String name;

    private Department department;

    private User appOwner;

    private Set<User> appAdmins;

    private boolean isFavorite;
}
