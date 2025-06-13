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
public class TotalAttributeVO {
    //属性个数
    private int number;

    //属性展示
    private List<AttributeVO> list;

}
