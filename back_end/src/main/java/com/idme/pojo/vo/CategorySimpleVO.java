package com.idme.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySimpleVO {

    private Long id;             // 分类ID
    private String businessCode; // 分类编码
    private String name;         // 分类中文名称
    private String nameEn;       // 分类英文名称
    private String description;  // 分类中文描述
    private String descriptionEn;// 分类英文描述
}
