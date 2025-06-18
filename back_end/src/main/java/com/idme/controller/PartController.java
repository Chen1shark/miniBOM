package com.idme.controller;

import com.idme.pojo.vo.PartVO;
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

    @GetMapping({"/{pageSize}/{curPage}/{searchType}/{searchText}","/{pageSize}/{curPage}"})
    public Result query(@PathVariable(required = false) String searchType, @PathVariable(required = false) String searchText, @PathVariable Integer pageSize, @PathVariable Integer curPage){
        List<PartVO> view = partService.query(searchType, searchText, pageSize, curPage);
        return Result.success(view);
    }
}