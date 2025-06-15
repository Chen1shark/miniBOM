package com.idme.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttributeVO {
    //属性id
    private Long id;

    //属性中文名
    private String name;

    //属性英文名
    private String nameEn;

    //属性中文描述
    private String description;

    //属性英文描述
    private String descriptionEn;

    //属性类型
    private String type;

    //属性状态
    private Boolean disableFlag;

    private String code;

    private Long classificationId;

    private List<Long> sourceIds;
}
