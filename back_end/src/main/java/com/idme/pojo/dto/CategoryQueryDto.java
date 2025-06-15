package com.idme.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryQueryDto {
    // 分页参数
    private Integer curPage;     // 当前页码
    private Integer pageSize;    // 每页大小

    // 分类编码查询条件
    private String code;         // 分类编码

    // 分类中文名称查询条件
    private String name;         // 分类中文名称

    // 分类描述中文查询条件
    private String description;

    // 分类状态查询条件
    private String status;

    // 分类描述查询条件
    private Long parentId;

    private Long id;
}