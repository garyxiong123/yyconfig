package com.yofish.apollo.dto;

import common.dto.NamespaceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author WangSongJun
 * @date 2020-01-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicProtectNamespaceDto {
    private List<NamespaceDTO> publicNamespaces;
    private List<NamespaceDTO> protectNamespaces;
}
