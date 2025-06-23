package com.idme.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BOMLinkSimpleDto {

    //BOMLink
    private Long bomLinkId;

    //数量
    private BigDecimal quantity;

    //目标实体
    private Long targetId;
}
