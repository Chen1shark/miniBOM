package com.idme.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.huawei.innovation.rdm.minibom.bean.enumerate.AssemblyMode;
import com.huawei.innovation.rdm.minibom.bean.enumerate.PartSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartBuildDto {
    private String name;        // 部件名称
    private String unit;       // 单位

    private PartSource source; // 来源

    private AssemblyMode partType; // 装配模式

    private String categoryId; // 分类id
}
