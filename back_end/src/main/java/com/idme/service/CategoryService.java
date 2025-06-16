package com.idme.service;

import com.alibaba.fastjson.JSONObject;
import com.huawei.innovation.rdm.xdm.dto.relation.EXADefinitionLinkViewDTO;
import com.idme.pojo.dto.CategoryQueryDto;
import com.idme.pojo.dto.CategoryUpdateDto;
import com.idme.pojo.vo.AttributeVO;
import com.idme.result.PageResult;

import com.idme.pojo.dto.CategoryCreateDto;
import com.idme.result.Result;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    /**
     * 分类分页查询
     * @param queryDto 包含分页参数和查询条件的DTO
     * @return 分页结果对象
     */
    PageResult queryCategories(CategoryQueryDto queryDto);

    /**
     * 创建分类
     * @param createDto 分类创建参数
     * @return 新建分类的ID
     */
    Long createCategory(CategoryCreateDto createDto);

    /**
     * 修改分类信息
     * @param updateDto 分类更新参数
     */
    void updateCategory(CategoryUpdateDto updateDto);

    /**
     * 删除分类
     * @param categoryId 分类ID
     */
    void deleteCategory(Long categoryId);

    List<EXADefinitionLinkViewDTO> batchCreateLinks(AttributeVO attributeVO);

    List<Map<String, Object>> queryAttribute(String linkId);

}