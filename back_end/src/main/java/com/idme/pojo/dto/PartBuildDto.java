package com.idme.pojo.dto;

import com.huawei.innovation.rdm.minibom.bean.enumerate.AssemblyMode;
import com.huawei.innovation.rdm.minibom.bean.enumerate.PartSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartBuildDto {
    private String name;        // 部件名称
    private String number;       // 型号

    private PartSource source; // 来源

    private AssemblyMode partType; // 装配模式

    private String categoryId; // 分类编码

    private Map<String, String> clsAttrs;//分类属性信息
}
