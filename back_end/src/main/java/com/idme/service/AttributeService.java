package com.idme.service;

import com.huawei.innovation.rdm.coresdk.basic.dto.PersistObjectIdsModifierDTO;
import com.huawei.innovation.rdm.xdm.dto.entity.EXADefinitionCreateDTO;
import com.huawei.innovation.rdm.xdm.dto.entity.EXADefinitionUpdateDTO;
import com.huawei.innovation.rdm.xdm.dto.entity.EXADefinitionViewDTO;


import java.util.List;

public interface AttributeService {
    /**
     * 分页查询
     */
    public List<EXADefinitionViewDTO> page(String searchText, Integer pageSize, Integer curPage);

    /**
     * 根据id查询属性
     * @param id
     * @return
     */
    List<EXADefinitionViewDTO> getById(Long id);

    /**
     * 修改属性
     * @param exaDefinitionUpdateDTO
     */
    EXADefinitionViewDTO update(EXADefinitionUpdateDTO exaDefinitionUpdateDTO);

    /**
     * 根据id删除属性
     * @param persistObjectIdsModifierDTO
     */
    void delete(PersistObjectIdsModifierDTO persistObjectIdsModifierDTO);


    /**
     * 新增属性
     * @param exaDefinitionCreateDTO
     * @return
     */
    EXADefinitionViewDTO create(EXADefinitionCreateDTO exaDefinitionCreateDTO);

    void category(Long id, Integer pageSize, Integer curPage);
}
