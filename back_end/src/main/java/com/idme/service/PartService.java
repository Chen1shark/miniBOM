package com.idme.service;

import com.idme.pojo.dto.PartBuildDto;

public interface PartService {

    Long createPart(PartBuildDto partBuildDTO);
}