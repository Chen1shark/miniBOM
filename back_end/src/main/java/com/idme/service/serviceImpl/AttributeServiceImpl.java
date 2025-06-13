package com.idme.service.serviceImpl;

import com.huawei.innovation.rdm.coresdk.basic.dto.PersistObjectIdsModifierDTO;
import com.huawei.innovation.rdm.coresdk.basic.enums.ConditionType;
import com.huawei.innovation.rdm.coresdk.basic.vo.QueryRequestVo;
import com.huawei.innovation.rdm.coresdk.basic.vo.RDMPageVO;
import com.huawei.innovation.rdm.xdm.delegator.EXADefinitionDelegator;
import com.huawei.innovation.rdm.xdm.dto.entity.EXADefinitionCreateDTO;
import com.huawei.innovation.rdm.xdm.dto.entity.EXADefinitionQueryViewDTO;
import com.huawei.innovation.rdm.xdm.dto.entity.EXADefinitionUpdateDTO;
import com.huawei.innovation.rdm.xdm.dto.entity.EXADefinitionViewDTO;
import com.idme.constant.AttributeConstant;
import com.idme.constant.MessageConstant;
import com.idme.exception.AttributeNotFoundException;
import com.idme.service.AttributeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class AttributeServiceImpl implements AttributeService {
    @Autowired
    public EXADefinitionDelegator exaDefinitionDelegator;


    /**
     * 分页查询
     */
    public List<EXADefinitionViewDTO> page(String searchText, Integer pageSize, Integer curPage) {
        QueryRequestVo queryRequestVo = new QueryRequestVo();
        if (StringUtils.hasText(searchText)) {
            queryRequestVo.addCondition(AttributeConstant.NAME, ConditionType.LIKE, searchText);
        }
        RDMPageVO rdmPageVO=new RDMPageVO();
        rdmPageVO.setCurPage(curPage);
        rdmPageVO.setPageSize(pageSize);
        List<EXADefinitionViewDTO> exaDefinitionViewDTOS = exaDefinitionDelegator.find(queryRequestVo, rdmPageVO);
        if(exaDefinitionViewDTOS == null){
            throw new AttributeNotFoundException(MessageConstant.ATTRIBUTE_NOT_FOUND);
        }
        return exaDefinitionViewDTOS;
    }

    /**
     * 根据id查询属性
     * @param id
     * @return
     */
    public List<EXADefinitionViewDTO> getById(Long id) {
        QueryRequestVo queryRequestVo = new QueryRequestVo();
        RDMPageVO rdmPageVO=new RDMPageVO();

        queryRequestVo.addCondition(AttributeConstant.ID, ConditionType.EQUAL, id);

        rdmPageVO.setCurPage(1);
        rdmPageVO.setPageSize(1);
        List<EXADefinitionViewDTO> exaDefinitionViewDTOS = exaDefinitionDelegator.find(queryRequestVo, rdmPageVO);

        if(exaDefinitionViewDTOS == null){
            throw new AttributeNotFoundException(MessageConstant.ATTRIBUTE_NOT_FOUND);
        }
        return exaDefinitionViewDTOS;
    }

    /**
     * 修改属性
     * @param exaDefinitionUpdateDTO
     * @return
     */
    public EXADefinitionViewDTO update(EXADefinitionUpdateDTO exaDefinitionUpdateDTO) {
        List<EXADefinitionViewDTO> list = getById(exaDefinitionUpdateDTO.getId());

        EXADefinitionViewDTO exaDefinitionViewDTO = list.get(0);

        // 填充字段
        exaDefinitionUpdateDTO.setName(exaDefinitionViewDTO.getName());
        exaDefinitionUpdateDTO.setType(exaDefinitionViewDTO.getType());
        exaDefinitionUpdateDTO.setConstraint(exaDefinitionViewDTO.getConstraint());
        exaDefinitionUpdateDTO.setCategory(exaDefinitionViewDTO.getCategory());

        EXADefinitionViewDTO updatedDto = exaDefinitionDelegator.update(exaDefinitionUpdateDTO);

        // 验证返回结果
        if (updatedDto == null) {
            throw new AttributeNotFoundException(MessageConstant.ATTRIBUTE_UPDATE_FAILED);
        }

        return updatedDto;
    }

    /**
     * 根据id删除属性
     * @param persistObjectIdsModifierDTO
     */
    public void delete(PersistObjectIdsModifierDTO persistObjectIdsModifierDTO) {

        exaDefinitionDelegator.batchDelete(persistObjectIdsModifierDTO);

    }

    /**
     * 新增
     * @param exaDefinitionCreateDTO
     * @return
     */
    public EXADefinitionViewDTO create(EXADefinitionCreateDTO exaDefinitionCreateDTO) {
        EXADefinitionCreateDTO createDTO = constraint(exaDefinitionCreateDTO);
        EXADefinitionViewDTO exaDefinitionViewDTO = exaDefinitionDelegator.create(createDTO);
        return exaDefinitionViewDTO;
    }

    @Override
    public void category(Long id, Integer pageSize, Integer curPage) {
        //TODO:实现根据属性查找分类
    }


    /**
     * 约束字段填充
     * @param exaDefinitionCreateDTO
     * @return
     */
    public EXADefinitionCreateDTO constraint(EXADefinitionCreateDTO exaDefinitionCreateDTO){

        switch(exaDefinitionCreateDTO.getType()){
            case "DECIMAL":{
                exaDefinitionCreateDTO.setConstraint("{\"associationType\":\"STRONG\",\"caseMode\":\"DEFAULT\",\"compose\":false,\"encryption\":false,\"graphIndex\":false,\"index\":false,\"legalValueType\":\"\",\"length\":0,\"multiValue\":false,\"notnull\":false,\"optionalValue\":\"LEGAL_VALUE_TYPE\",\"precision\":10,\"range\":\"\",\"secretLevel\":\"internal\",\"stockInDB\":true,\"variable\":true}");
                break;
            }
            case "STRING":{
                exaDefinitionCreateDTO.setConstraint("{\"associationType\":\"STRONG\",\"caseMode\":\"DEFAULT\",\"compose\":false,\"encryption\":false,\"graphIndex\":false,\"index\":false,\"legalValueType\":\"\",\"length\":200,\"multiValue\":false,\"notnull\":false,\"optionalValue\":\"LEGAL_VALUE_TYPE\",\"precision\":0,\"secretLevel\":\"internal\",\"stockInDB\":true,\"variable\":true}");
                break;
            }
            case "INTEGER":{
                exaDefinitionCreateDTO.setConstraint("{\"associationType\":\"STRONG\",\"caseMode\":\"DEFAULT\",\"compose\":false,\"encryption\":false,\"graphIndex\":false,\"index\":false,\"legalValueType\":\"\",\"length\":0,\"multiValue\":false,\"notnull\":false,\"optionalValue\":\"LEGAL_VALUE_TYPE\",\"precision\":0,\"range\":\"\",\"secretLevel\":\"internal\",\"stockInDB\":true,\"variable\":true}");
                break;
            }
        }
        return exaDefinitionCreateDTO;
    }



}
