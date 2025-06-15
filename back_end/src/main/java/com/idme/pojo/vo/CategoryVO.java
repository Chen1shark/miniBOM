package com.idme.pojo.vo;

import com.huawei.innovation.rdm.coresdk.extattrmgmt.dto.EXAValueViewDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVO {
    private Long id;             // 分类ID
    private String code;         // 分类编码
    private String name;         // 分类中文名称
    private String nameEn;       // 分类英文名称
    private String description;  // 分类中文描述
    private String descriptionEn;// 分类英文描述
    private Boolean status;      // 分类状态
    private Long parentId;     // 父节点ID
    private List<EXAValueViewDTO> extAttrs;
}