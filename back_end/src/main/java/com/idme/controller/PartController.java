package com.idme.controller;

import com.huawei.innovation.rdm.minibom.dto.entity.PartCreateDTO;
import com.huawei.innovation.rdm.minibom.dto.entity.PartQueryViewDTO;
import com.huawei.innovation.rdm.minibom.dto.entity.PartViewDTO;
import com.idme.result.Result;
import com.idme.service.PartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.idme.pojo.dto.PartBuildDto;

import java.util.List;

@Slf4j
@RequestMapping("/part")
@RestController
public class PartController {

    @Autowired
    PartService partService;


    /**
     * 添加part（此时未添加分类）
     * @param partBuildDto
     * @return id
     */
    @PostMapping("/create")
    public Result creat(@RequestBody PartBuildDto partBuildDto){
        Long partId = partService.createPart(partBuildDto);
        return Result.success(partId);
    }

    @GetMapping("/query")
    public Result query(){
        List<PartQueryViewDTO> view = partService.query();
        return Result.success(view);
    }
}