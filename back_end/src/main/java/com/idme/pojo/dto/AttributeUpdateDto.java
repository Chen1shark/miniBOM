package com.idme.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeUpdateDto {

    //属性id
    private Long id;

    //属性中文描述
    private String description;

    //属性英文描述
    private String descriptionEn;

    //属性状态
    private Boolean disableFlag;
}
