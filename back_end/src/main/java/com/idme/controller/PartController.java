package com.idme.controller;

import com.huawei.innovation.rdm.minibom.dto.entity.PartCreateDTO;
import com.idme.result.Result;
import com.idme.service.PartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.idme.pojo.dto.PartBuildDto;

@Slf4j
@RequestMapping("/part")
@RestController
public class PartController {

    @Autowired
    PartService partService;

    @PostMapping("/create")
    public Result register(@RequestBody PartBuildDto partBuildDto){
        Long partId = partService.createPart(partBuildDto);
        return Result.success(partId);
    }
}