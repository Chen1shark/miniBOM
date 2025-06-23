package com.idme.pojo.vo;

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
public class BOMVO {
    private Long bomLinkId;//BOMLink的id

    private String referenceDesignator;//位号

    private String partId; //部件编码

    private String name; //部件名称

    private String version; //版本号

    private AssemblyMode partType; //装配模式

    private PartSource source; //来源

    private String businessCode; //分类编码

    private String partMasterId;

    private String parBranchId;
}
