package com.idme.controller;

import com.huawei.innovation.rdm.coresdk.basic.dto.PersistObjectIdsModifierDTO;
import com.idme.pojo.dto.PartUpdateDto;
import com.idme.pojo.vo.PartVO;
import com.idme.pojo.vo.PartVersionVO;
import com.idme.result.Result;
import com.idme.service.PartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.idme.pojo.dto.PartBuildDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RequestMapping("/part")
@RestController
public class PartController {

    @Autowired
    PartService partService;

    /**
     * 添加part
     * @param partBuildDto
     * @return
     */
    @PostMapping("/create")
    public Result creat(@RequestBody PartBuildDto partBuildDto){
        Long partId = partService.createPart(partBuildDto);
        return Result.success(partId);
    }

    /**
     * 更新part
     * @param partUpdateDto
     * @return
     */
    @PostMapping("/update")
    public Result update(@RequestBody PartUpdateDto partUpdateDto){
        Long partId = partService.updatePart(partUpdateDto);
        return Result.success(partId);
    }

    /**
     * 分页查询
     * @param pageSize
     * @param curPage
     * @param searchType
     * @param searchText
     * @param isFilterOld
     * @return
     */
    @GetMapping("/{pageSize}/{curPage}")
    public Result query(
            @PathVariable Integer pageSize,
            @PathVariable Integer curPage,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false, defaultValue = "false") Boolean isFilterOld
    ){
        // 声明为可变容器类型
        AtomicLong count = new AtomicLong(0L);
        count.set(0L);

        List<PartVO> partVOS = partService.query(searchType, searchText, pageSize, curPage, count, isFilterOld);// 在query方法内修改值

        Map<String, Object> view = new HashMap<>();
        view.put("count", count.get());
        view.put("list", partVOS);
        return Result.success(view);
    }

    /**
     * 查询版本信息
     * @param masterId
     * @param pageSize
     * @param curPage
     * @return
     */
    @GetMapping({"/version/{pageSize}/{curPage}/{masterId}"})
    public Result versionQuery(@PathVariable Long masterId, @PathVariable Integer pageSize, @PathVariable Integer curPage){
        List<PartVersionVO> partVersionVOS = partService.versionQuery(masterId, pageSize, curPage);// 在query方法内修改值

        Map<String, Object> view = new HashMap<>();
        view.put("count", partVersionVOS.size());
        view.put("list", partVersionVOS);
        return Result.success(view);
    }

    @DeleteMapping("/delete")
    public Result delete(@RequestBody Long masterId){
        partService.deleteByMasterId(masterId);
        return Result.success();
    }
}