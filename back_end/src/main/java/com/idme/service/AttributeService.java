package com.idme.service;

import com.huawei.innovation.rdm.xdm.dto.entity.EXADefinitionViewDTO;


import java.util.List;

public interface AttributeService {
    /**
     * 分页查询
     */
    public List<EXADefinitionViewDTO> page(String searchText, Integer pageSize, Integer curPage);
}
