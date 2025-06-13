package com.idme.controller;

import com.huawei.innovation.rdm.coresdk.basic.dto.PersistObjectIdsModifierDTO;
import com.huawei.innovation.rdm.xdm.dto.entity.*;
import com.idme.pojo.dto.AttributeUpdateDto;
import com.idme.pojo.vo.AttributeVO;
import com.idme.pojo.vo.CategoryVO;
import com.idme.pojo.vo.TotalAttributeVO;
import com.idme.result.Result;
import com.idme.service.AttributeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/attribute")
@RestController
public class AttributeController {

    @Autowired
    public AttributeService attributeService;

    /**
     * 属性查询
     * @param searchText
     * @param pageSize
     * @param curPage
     * @return
     */
    @GetMapping({"/{pageSize}/{curPage}/{searchText}","/{pageSize}/{curPage}"})
    public Result<TotalAttributeVO> page(@PathVariable(required = false) String searchText, @PathVariable Integer pageSize, @PathVariable Integer curPage){
        List<EXADefinitionViewDTO> list = attributeService.page(searchText, pageSize, curPage);
        List<AttributeVO> voList = list.stream()
                .map(dto -> {
                    AttributeVO vo = new AttributeVO();
                    vo.setId(dto.getId());
                    vo.setName(dto.getName());
                    vo.setNameEn(dto.getNameEn());
                    vo.setDescription(dto.getDescription());
                    vo.setDescriptionEn(dto.getDescriptionEn());
                    vo.setType(dto.getType());
                    vo.setDisableFlag(dto.getDisableFlag());
                    return vo;
                })
                .collect(Collectors.toList());
        //记录总数
        TotalAttributeVO totalAttributeVO = TotalAttributeVO.builder()
                .list(voList)
                .number(list.size())
                .build();
        return Result.success(totalAttributeVO);
    }

    /**
     * 根据id查询属性
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<AttributeVO> getById(@PathVariable Long id){
        List<EXADefinitionViewDTO> list = attributeService.getById(id);
        AttributeVO attributeVO = new AttributeVO();
        BeanUtils.copyProperties(list.get(0),attributeVO);
        return Result.success(attributeVO);
    }


    /**
     * 修改属性
     * @param attributeUpdateDto
     * @return
     */
    @PostMapping
    public Result<AttributeUpdateDto> update(@RequestBody AttributeUpdateDto attributeUpdateDto){
        EXADefinitionUpdateDTO updateDTO = new EXADefinitionUpdateDTO();
        BeanUtils.copyProperties(attributeUpdateDto, updateDTO);
        EXADefinitionViewDTO dto = attributeService.update(updateDTO);

        // 回填更新后的数据
        BeanUtils.copyProperties(dto, attributeUpdateDto);

        return Result.success(attributeUpdateDto);
    }

    /**
     * 根据id删除字段
     * @param persistObjectIdsModifierDTO
     * @return
     */
    @DeleteMapping
    public Result delete(@RequestBody PersistObjectIdsModifierDTO persistObjectIdsModifierDTO){
        attributeService.delete(persistObjectIdsModifierDTO);
        return Result.success();
    }

    /**
     * 新增属性
     * @param exaDefinitionCreateDTO
     * @return
     */
    @PostMapping("/create")
    public Result<AttributeVO> create(@RequestBody EXADefinitionCreateDTO exaDefinitionCreateDTO){
        EXADefinitionViewDTO exaDefinitionViewDTO = attributeService.create(exaDefinitionCreateDTO);
        AttributeVO attributeVO = new AttributeVO();
        BeanUtils.copyProperties(exaDefinitionViewDTO,attributeVO);
        return Result.success(attributeVO);
    }


    @GetMapping("/category/{id}/{pageSize}/{curPage}")
    public Result<CategoryVO> category(@PathVariable Long id,@PathVariable Integer pageSize, @PathVariable Integer curPage){
        // TODO:实现根据属性查找分类功能
        attributeService.category(id,pageSize,curPage);

        return null;
    }

}
