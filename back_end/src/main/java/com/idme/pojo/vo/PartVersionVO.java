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
public class PartVersionVO {
    private Long partId; //部件编码

    private String name; //部件名称

    private String version; //版本号

    private boolean latestIteration; //是否最新版本
}
