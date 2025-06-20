package com.idme.controller;

import com.idme.pojo.dto.BOMDeleteDto;
import com.idme.pojo.dto.BOMLinkDto;
import com.idme.pojo.dto.BOMLinkSimpleDto;
import com.idme.pojo.dto.BOMUpdateDto;
import com.idme.pojo.vo.BOMTreeVO;
import com.idme.pojo.vo.BOMVO;
import com.idme.pojo.vo.PartVO;
import com.idme.pojo.vo.PartVersionVO;
import com.idme.result.Result;
import com.idme.service.BOMService;
import com.idme.service.PartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping("/bom")
@RestController
public class BOMController {

    @Autowired
    private BOMService bomService;

    @Autowired
    private PartService partService;

    /**
     * 批量增加BOMLink
     * @param bomLinkDtos
     * @return
     */
    @PostMapping("/add")
    public Result add(@RequestBody List<BOMLinkDto> bomLinkDtos){
        bomService.add(bomLinkDtos);
        return Result.success();
    }


    /**
     * 根据targetId获取source
     * @param partMasterId
     * @return
     */
    @GetMapping("/target/{partMasterId}")
    public Result<List<PartVO>> getSource(@PathVariable Long partMasterId){
        List<Long> sequenceNumberById = bomService.getSourceId(partMasterId);

        // 声明为可变容器类型
        AtomicLong count = new AtomicLong(0L);
        count.set(0L);

        List<PartVO> partVOS = sequenceNumberById.stream()
                .map(id -> partService.query("id",String.valueOf(id), 1, 1, count, true).get(0))
                .collect(Collectors.toList());

        return Result.success(partVOS);
    }

    /**
     * 根据sourceId获取target
     * @param partId
     * @return
     */

    @GetMapping("/source/{partId}")
    public Result<List<BOMVO>> getTarget(@PathVariable Long partId){
        List<BOMLinkSimpleDto> bomLinkDetails = bomService.getBOMLinkDetails(partId);

        List<BOMVO> bomVOs = new ArrayList<>();

        for(BOMLinkSimpleDto bomLinkDetail: bomLinkDetails){
            PartVersionVO partVersionVO = partService.versionQuery(bomLinkDetail.getTargetId(), 1, 1, true).get(0);
            String referenceDesignator = bomService.getReferenceDesignator(bomLinkDetail.getBomLinkId());

            // 声明为可变容器类型
            AtomicLong count = new AtomicLong(0L);
            count.set(0L);

            PartVO partVO= partService.query("id", String.valueOf(partVersionVO.getPartId()), 1, 1, count, true).get(0);
            BOMVO bomVO = new BOMVO();
            BeanUtils.copyProperties(partVO,bomVO);
            bomVO.setBomLinkId(bomLinkDetail.getBomLinkId());
            bomVO.setReferenceDesignator(referenceDesignator);
            bomVOs.add(bomVO);
        }

        return Result.success(bomVOs);
    }

    /**
     * 更新BOM
     * @param bomUpdateDto
     * @return
     */
    @PostMapping("/update")
    private Result update(@RequestBody BOMUpdateDto bomUpdateDto){
        bomService.update(bomUpdateDto);
        return Result.success();
    }


    /**
     * 根据BOMLink删除bom
     * @param bomDeleteDto
     * @return
     */
    @DeleteMapping("/delete")
    private Result delete(@RequestBody BOMDeleteDto bomDeleteDto){
        bomService.delete(bomDeleteDto.getIds());
        return Result.success();
    }

    /**
     * 查询BOM清单
     * @return
     */
    @GetMapping("/checklist")
    public Result<BOMTreeVO> getChecklist(){

        return Result.success(bomService.getChecklist());

    }


}
