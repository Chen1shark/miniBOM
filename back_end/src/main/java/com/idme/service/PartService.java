package com.idme.service;

import com.huawei.innovation.rdm.minibom.dto.entity.PartQueryViewDTO;
import com.idme.pojo.dto.PartBuildDto;
import com.idme.pojo.vo.PartVO;

import java.util.List;

public interface PartService {

    Long createPart(PartBuildDto partBuildDTO);

    List<PartVO> query(String searchType, String searchText, Integer pageSize, Integer curPage);
}