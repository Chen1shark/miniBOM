package com.idme.service;

import com.huawei.innovation.rdm.minibom.dto.entity.PartQueryViewDTO;
import com.idme.pojo.dto.PartBuildDto;

import java.util.List;

public interface PartService {

    Long createPart(PartBuildDto partBuildDTO);

    List<PartQueryViewDTO> query();
}