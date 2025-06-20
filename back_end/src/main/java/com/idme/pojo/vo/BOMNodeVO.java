package com.idme.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BOMNodeVO {

    private Long partId;

    private Long partMasterId;

    private String name;

    //添加子节点列表
    private List<BOMNodeVO> children;

}
