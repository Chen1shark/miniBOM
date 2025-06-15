package com.idme.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateDto {
    private Long id;
    private String name;          // 中文名称（如"台式机"）
    private String nameEn;        // 英文名称（如"DesktopComputer"）
    private String description;   // 中文描述
    private String descriptionEn; // 英文描述
}