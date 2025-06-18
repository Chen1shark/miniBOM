package com.idme.controller;

import com.idme.pojo.dto.AttributeIdDto;
import com.idme.pojo.dto.CategoryQueryDto;
import com.idme.pojo.dto.CategoryUpdateDto;
import com.idme.pojo.vo.AttributeVO;
import com.idme.result.PageResult;
import com.idme.result.Result;
import com.idme.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import com.idme.pojo.dto.CategoryCreateDto;

import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/category")
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分类分页查询接口
     *
     * @param queryDto 包含分页参数的请求体
     * @return 分页结果
     */
    @PostMapping("/pageQuery")
    public Result<PageResult> pageQueryCategories(@RequestBody CategoryQueryDto queryDto) {
        PageResult result = categoryService.queryCategories(queryDto);
        return Result.success(result);
    }

    /**
     * 全量查询接口（不带条件，默认第一页10条）
     *
     * @return 分类列表
     */
    @PostMapping("/all")
    public Result<PageResult> allCategories() {
        CategoryQueryDto queryDto = CategoryQueryDto.builder()
                .curPage(1)
                .pageSize(10)
                .build();
        PageResult result = categoryService.queryCategories(queryDto);
        return Result.success(result);
    }

    /**
     * 按分类编码查询
     *
     * @param code 分类编码
     * @return 分页结果
     */
    @GetMapping("/code/{code}")
    public Result<PageResult> getCategoryByCode(@PathVariable String code) {
        CategoryQueryDto queryDto = CategoryQueryDto.builder()
                .code(code)
                .curPage(1)
                .pageSize(10)
                .build();
        PageResult result = categoryService.queryCategories(queryDto);
        return Result.success(result);
    }

    /**
     * 按分类名称查询
     *
     * @return 分页结果
     */
    @GetMapping("/query")
    public Result<PageResult> getCategoryByOption(CategoryQueryDto queryDto) {
        queryDto.builder()
                .curPage(queryDto.getCurPage())
                .pageSize(queryDto.getPageSize())
                .name(queryDto.getName())
                .code(queryDto.getCode())
                .build();
        PageResult result = categoryService.queryCategories(queryDto);
        return Result.success(result);
    }

    /**
     * 创建分类
     *
     * @param createDto 分类创建参数
     * @return 创建结果
     */
    @PostMapping("/create")
    public Result<Long> createCategory(@RequestBody CategoryCreateDto createDto) {
        Long categoryId = categoryService.createCategory(createDto);
        return Result.success(categoryId);
    }

    /**
     * 修改分类修改name、nameEn、description、descriptionEn
     *
     * @param updateDto 分类创建参数
     * @return 创建结果
     */
    @PostMapping("/updateCategory")
    public Result<Void> updateCategory(@RequestBody CategoryUpdateDto updateDto) {
        categoryService.updateCategory(updateDto);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable("id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return Result.success();
    }



    @PostMapping("/batchCreateLinks")
    public Result batchCreateLinks(@RequestBody AttributeVO attributeVO) {

        return Result.success(categoryService.batchCreateLinks(attributeVO));


    }

    @GetMapping("/queryAttribute")
    public List<Map<String, Object>>  queryAttribute(String linkId) {

        return categoryService.queryAttribute(linkId);
    }

    /**
     * 删除分类下属性
     * @param attributeIdDto
     * @return
     */
    @DeleteMapping("/deleteAttribute")
    public Result deleteAttribute(@RequestBody AttributeIdDto attributeIdDto){

        categoryService.deleteAttribute(attributeIdDto);

        return Result.success();
    }

}
