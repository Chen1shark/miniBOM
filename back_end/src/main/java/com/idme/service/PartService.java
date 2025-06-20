package com.idme.service;

import com.idme.pojo.dto.PartBuildDto;
import com.idme.pojo.dto.PartUpdateDto;
import com.idme.pojo.vo.PartVO;
import com.idme.pojo.vo.PartVersionVO;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public interface PartService {

    Long createPart(PartBuildDto partBuildDTO);

    Long updatePart(PartUpdateDto partUpdateDto);

    List<PartVO> query(String searchType,
                       String searchText,
                       Integer pageSize,
                       Integer curPage,
                       AtomicLong count,
                       Boolean isFilterOld);

    Long checkOutPart(Long masterId);

    void checkInPart(Long masterId);

    List<PartVersionVO> versionQuery(Long masterId, Integer pageSize, Integer curPage, Boolean isFilterOld);

    void deleteNewByMasterId(Long masterId);

    void deleteAllByMasterId(Long masterId);
}