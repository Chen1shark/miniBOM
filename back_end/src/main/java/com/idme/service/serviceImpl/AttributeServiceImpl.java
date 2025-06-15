package com.idme.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huawei.innovation.rdm.coresdk.basic.dto.PersistObjectIdsModifierDTO;
import com.huawei.innovation.rdm.coresdk.basic.enums.ConditionType;
import com.huawei.innovation.rdm.coresdk.basic.vo.QueryRequestVo;
import com.huawei.innovation.rdm.coresdk.basic.vo.RDMPageVO;
import com.huawei.innovation.rdm.xdm.delegator.EXADefinitionDelegator;
import com.huawei.innovation.rdm.xdm.dto.entity.*;
import com.idme.constant.AttributeConstant;
import com.idme.constant.CategoryConstant;
import com.idme.constant.MessageConstant;
import com.idme.context.BaseContext;
import com.idme.exception.AttributeNotFoundException;
import com.idme.exception.CategoryNotFoundException;
import com.idme.pojo.vo.CategorySimpleVO;
import com.idme.service.AttributeService;
import com.idme.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

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
     * 根据id查找属性
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

    /**
     * 根据属性id查分类
     * @param id
     * @return
     */
    public List<CategorySimpleVO> category(Long id) {
        String url = "https://dme.cn-north-4.huaweicloud.com/rdm_b49541bdd3de4658aab470544248649c_app/publicservices/rdm/basic/api/EXADefinition/queryExtendedAttributeRefered";

        // 构建请求体
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        requestBody.put("params", params);

        //构建请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Auth-Token", BaseContext.getCurrentToken());

        try {
            HttpClientUtil.HttpResponse response = HttpClientUtil.doPost4JsonWithHeaders(url, requestBody, headers);


            // 解析完整响应结构
            Map<String, Object> responseMap = JSON.parseObject(response.getBody(),
                    new TypeReference<Map<String, Object>>() {});

            // 获取data数组
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) responseMap.get("data");


            Map<String, Object> firstData = dataList.get(0);

            List<Map<String, Object>> categoryNodes = (List<Map<String, Object>>) firstData.get("categorynodeInfo");

            // 转换为CategorySimpleVO列表
            List<CategorySimpleVO> categoryList = categoryNodes.stream().map(node -> {
                CategorySimpleVO vo = new CategorySimpleVO();
                vo.setId(Long.parseLong(node.get(CategoryConstant.ID).toString()));
                vo.setBusinessCode(node.get(CategoryConstant.BUSINESS_CODE).toString());
                vo.setName(node.get(CategoryConstant.NAME).toString());
                vo.setNameEn(node.get(CategoryConstant.NAME_EN).toString());
                vo.setDescription(Objects.toString(node.get(CategoryConstant.DESCRIPTION), null));
                vo.setDescriptionEn(Objects.toString(node.get(CategoryConstant.DESCRIPTION_EN), null));
                return vo;
            }).collect(Collectors.toList());


            return categoryList;
        }catch(RuntimeException ex){
            throw new CategoryNotFoundException(MessageConstant.CATEGORY_NOT_FOUND);
        }

    }

    /**
     * 属性总数
     * @param searchText
     * @return
     */
    public long count(String searchText) {
        QueryRequestVo queryRequestVo = new QueryRequestVo();

        if (StringUtils.hasText(searchText)) {
            queryRequestVo.addCondition(AttributeConstant.NAME, ConditionType.LIKE, searchText);
        }

        long count = exaDefinitionDelegator.count(queryRequestVo);

        return count;
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
