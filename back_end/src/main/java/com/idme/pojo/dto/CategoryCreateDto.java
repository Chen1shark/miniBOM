package com.idme.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryCreateDto {
    private Long id;
    private String code;          // 分类编码（如CN00000013）
    private String name;          // 中文名称（如"台式机"）
    private String nameEn;        // 英文名称（如"DesktopComputer"）
    private String description;   // 中文描述
    private String descriptionEn; // 英文描述
    private Boolean status;       // 分类状态（true表示启用）
    private Long parentId;
    private List<CategoryCreateDto> detailList;//子节点
}