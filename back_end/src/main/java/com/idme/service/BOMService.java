package com.idme.service;

import com.idme.pojo.dto.BOMLinkDto;
import com.idme.pojo.dto.BOMLinkSimpleDto;
import com.idme.pojo.dto.BOMUpdateDto;

import java.util.List;

public interface BOMService {

    /**
     * 批量增加BOMLink
     * @param bomLinkDtos
     */
    void add(List<BOMLinkDto> bomLinkDtos);

    /**
     * 根据targetId获取sourceId
     * @param targetId
     * @return
     */

    List<Long> getSourceId(Long targetId);

    /**
     * 根据sourceId获取关联的BOMLink详情（包含bomLinkId和targetId）
     * @param sourceId
     * @return List<BOMLinkSimpleDto>
     */
    public List<BOMLinkSimpleDto> getBOMLinkDetails(Long sourceId);

    /**
     * 根据BOMLink查位号
     * @param bomLinkId
     * @return
     */
    public String getReferenceDesignator(Long bomLinkId);

    /**
     * 更新BOM
     * @param bomUpdateDto
     */
    void update(BOMUpdateDto bomUpdateDto);

    void delete(List<Long> ids);
}
