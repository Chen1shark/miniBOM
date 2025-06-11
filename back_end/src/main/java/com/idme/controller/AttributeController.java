package com.idme.controller;

import com.huawei.innovation.rdm.xdm.delegator.EXADefinitionDelegator;
import com.huawei.innovation.rdm.xdm.dto.entity.*;
import com.idme.pojo.vo.AttributeVO;
import com.idme.pojo.vo.TotalAttributeVO;
import com.idme.result.Result;
import com.idme.service.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
                    return vo;
                })
                .collect(Collectors.toList());
        TotalAttributeVO totalAttributeVO = TotalAttributeVO.builder()
                .list(voList)
                .number(list.size())
                .build();
        return Result.success(totalAttributeVO);
    }
}
