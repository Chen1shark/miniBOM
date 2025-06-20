package com.idme.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BOMLinkSimpleDto {

    //BOMLink
    private Long bomLinkId;

    //目标实体
    private Long targetId;
}
