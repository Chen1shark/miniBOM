package com.idme.service.serviceImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huawei.innovation.rdm.coresdk.basic.dto.GenericLinkQueryDTO;
import com.huawei.innovation.rdm.coresdk.basic.dto.ObjectReferenceParamDTO;
import com.huawei.innovation.rdm.coresdk.basic.dto.PersistObjectIdModifierDTO;
import com.huawei.innovation.rdm.coresdk.basic.dto.PersistObjectIdsModifierDTO;
import com.huawei.innovation.rdm.coresdk.basic.enums.ConditionType;
import com.huawei.innovation.rdm.coresdk.basic.vo.QueryRequestVo;
import com.huawei.innovation.rdm.coresdk.basic.vo.RDMPageVO;
import com.huawei.innovation.rdm.minibom.delegator.BOMLinkDelegator;
import com.huawei.innovation.rdm.minibom.delegator.BOMUsesOccurrenceDelegator;
import com.huawei.innovation.rdm.minibom.dto.entity.BOMUsesOccurrenceCreateDTO;
import com.huawei.innovation.rdm.minibom.dto.entity.BOMUsesOccurrenceUpdateDTO;
import com.huawei.innovation.rdm.minibom.dto.entity.BOMUsesOccurrenceViewDTO;
import com.huawei.innovation.rdm.minibom.dto.relation.BOMLinkCreateDTO;
import com.huawei.innovation.rdm.minibom.dto.relation.BOMLinkUpdateDTO;
import com.huawei.innovation.rdm.minibom.dto.relation.BOMLinkViewDTO;
import com.idme.constant.AttributeConstant;
import com.idme.constant.BOMConstant;
import com.idme.context.BaseContext;
import com.idme.pojo.dto.BOMLinkDto;
import com.idme.pojo.dto.BOMLinkSimpleDto;
import com.idme.pojo.dto.BOMUpdateDto;
import com.idme.service.BOMService;
import com.idme.service.PartService;
import com.idme.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Slf4j
@Service
public class BOMServiceImpl implements BOMService {

    @Autowired
    private BOMUsesOccurrenceDelegator bomUsesOccurrenceDelegator;

    @Autowired
    private BOMLinkDelegator bomLinkDelegator;


    /**
     * 批量增加BOMLink
     * @param bomLinkDtos
     */
    @Transactional
    public void add(List<BOMLinkDto> bomLinkDtos) {
        //BOMLink创建
        List<BOMLinkCreateDTO> bomLinkCreateDTOs = bomLinkDtos.stream()
                .map(dto -> {
                    BOMLinkCreateDTO createDTO = new BOMLinkCreateDTO();

                    ObjectReferenceParamDTO objectReferenceParamDTO1 = new ObjectReferenceParamDTO();
                    objectReferenceParamDTO1.setId(dto.getSourceId());
                    ObjectReferenceParamDTO objectReferenceParamDTO2 = new ObjectReferenceParamDTO();
                    objectReferenceParamDTO2.setId(dto.getTargetId());

                    createDTO.setSource(objectReferenceParamDTO1);
                    createDTO.setTarget(objectReferenceParamDTO2);

                    createDTO.setQuantity(dto.getQuantity());


                    return createDTO;
                })
                .collect(Collectors.toList());
        List<BOMLinkViewDTO> bomLinkViewDTOS = bomLinkDelegator.batchCreate(bomLinkCreateDTOs);

        // BOMUsesOccurrence创建
        List<BOMUsesOccurrenceCreateDTO> bomUsesOccurrenceCreateDTOS = IntStream.range(0, bomLinkViewDTOS.size())
                .mapToObj(index -> {
                    BOMLinkViewDTO dto = bomLinkViewDTOS.get(index);

                    BOMUsesOccurrenceCreateDTO bomUsesOccurrenceCreateDTO = new BOMUsesOccurrenceCreateDTO(); // 修正构造函数调用

                    ObjectReferenceParamDTO objectReferenceParamDTO = new ObjectReferenceParamDTO();
                    objectReferenceParamDTO.setId(dto.getId());

                    bomUsesOccurrenceCreateDTO.setBomLink(objectReferenceParamDTO);
                    // 使用当前索引从另一个列表中获取referenceDesignator
                    bomUsesOccurrenceCreateDTO.setReferenceDesignator(bomLinkDtos.get(index).getReferenceDesignator());

                    return bomUsesOccurrenceCreateDTO; // 添加返回语句
                })
                .collect(Collectors.toList());
        bomUsesOccurrenceDelegator.batchCreate(bomUsesOccurrenceCreateDTOS);
        return;
    }

    /**
     * 根据targetId获取sourceId
     * @param targetId
     * @return
     */
    public List<Long> getSourceId(Long targetId) {

        String url = "https://dme.cn-north-4.huaweicloud.com/rdm_b49541bdd3de4658aab470544248649c_app/publicservices/api/BOMLink/queryRelationship/100/1";

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Auth-Token", BaseContext.getCurrentToken());

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> params = new HashMap<>();


        params.put("objectId",targetId);
        requestBody.put("params",params);
        params.put("role",BOMConstant.target);

        HttpClientUtil.HttpResponse response = HttpClientUtil.doPost4JsonWithHeaders(url, requestBody, headers);

        if(response.getBody()==null){
            return Collections.emptyList();
        }

        // 解析响应体为JSON对象
        Map<String, Object> responseMap = JSON.parseObject(response.getBody(),
                new TypeReference<Map<String, Object>>(){});


        // 安全获取data字段（处理可能为对象或数组的情况）
        Object dataObj = responseMap.get("data");
        if (dataObj == null) {
            return Collections.emptyList();
        }



        List<Map<String, Object>> dataList;
        if (dataObj instanceof List) {
            dataList = (List<Map<String, Object>>) dataObj;
        } else if (dataObj instanceof Map) {
            dataList = Collections.singletonList((Map<String, Object>) dataObj);
        } else {
            log.error("未知的data类型: {}", dataObj.getClass());
            return Collections.emptyList();
        }

        log.info(dataList.toString());
        // 提取sourceId
        return dataList.stream()
                .filter(dataItem -> dataItem != null && dataItem.containsKey("source"))
                .flatMap(dataItem -> {
                    Object sourceObj = dataItem.get("source");
                    if (sourceObj instanceof Map) {
                        Map<?,?> sourceMap = (Map<?,?>) sourceObj;
                        Object id = sourceMap.get("id");

                        // 兼容String和Number类型
                        if (id instanceof Number) {
                            return Stream.of(((Number) id).longValue());
                        } else if (id instanceof String) {
                            try {
                                return Stream.of(Long.parseLong((String) id));
                            } catch (NumberFormatException e) {
                                log.warn("ID格式错误: {}", id);
                            }
                        }
                    }
                    return Stream.empty();
                })
                .collect(Collectors.toList());



    }


    /**
     * 根据sourceId获取关联的BOMLink详情（包含bomLinkId和targetId）
     * @param sourceId
     * @return List<BOMLinkSimpleDto>
     */
    public List<BOMLinkSimpleDto> getBOMLinkDetails(Long sourceId) {
        String url = "https://dme.cn-north-4.huaweicloud.com/rdm_b49541bdd3de4658aab470544248649c_app/publicservices/api/BOMLink/queryRelationship/100/1";

        Map<String, String> headers = new HashMap<>();
        headers.put("X-Auth-Token", BaseContext.getCurrentToken());

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("objectId", sourceId);
        requestBody.put("params", params);
        params.put("role", BOMConstant.SOURCE);

        HttpClientUtil.HttpResponse response = HttpClientUtil.doPost4JsonWithHeaders(url, requestBody, headers);

        if(response.getBody() == null) {
            return Collections.emptyList();
        }

        // 解析响应体
        Map<String, Object> responseMap = JSON.parseObject(response.getBody(),
                new TypeReference<Map<String, Object>>(){});

        // 处理data字段
        Object dataObj = responseMap.get("data");
        if(dataObj == null) {
            return Collections.emptyList();
        }

        // 转换为统一列表格式
        List<Map<String, Object>> dataList;
        if(dataObj instanceof List) {
            dataList = (List<Map<String, Object>>) dataObj;
        } else if(dataObj instanceof Map) {
            dataList = Collections.singletonList((Map<String, Object>) dataObj);
        } else {
            log.error("未知的data类型: {}", dataObj.getClass());
            return Collections.emptyList();
        }

        // 提取bomLinkId和targetId
        return dataList.stream()
                .filter(dataItem -> dataItem != null)
                .map(dataItem -> {
                    BOMLinkSimpleDto dto = new BOMLinkSimpleDto();

                    // 设置BOMLinkId（data下的直接id字段）
                    Object bomLinkId = dataItem.get("id");
                    if(bomLinkId instanceof Number) {
                        dto.setBomLinkId(((Number)bomLinkId).longValue());
                    } else if(bomLinkId instanceof String) {
                        try {
                            dto.setBomLinkId(Long.parseLong((String)bomLinkId));
                        } catch(NumberFormatException e) {
                            log.warn("BOMLinkId格式错误: {}", bomLinkId);
                        }
                    }

                    // 设置targetId（从target对象获取）
                    if(dataItem.containsKey("target")) {
                        Object targetObj = dataItem.get("target");
                        if(targetObj instanceof Map) {
                            Map<?,?> targetMap = (Map<?,?>) targetObj;
                            Object targetIdObj = targetMap.get("id");
                            if(targetIdObj instanceof Number) {
                                dto.setTargetId(((Number)targetIdObj).longValue());
                            } else if(targetIdObj instanceof String) {
                                try {
                                    dto.setTargetId(Long.parseLong((String)targetIdObj));
                                } catch(NumberFormatException e) {
                                    log.warn("targetId格式错误: {}", targetIdObj);
                                }
                            }
                        }
                    }

                    return dto;
                })
                .filter(dto -> dto.getBomLinkId() != null && dto.getTargetId() != null)
                .collect(Collectors.toList());
    }

    /**
     * 根据BOMLink查位号
     * @param bomLinkId
     * @return
     */
    public String getReferenceDesignator(Long bomLinkId){

        QueryRequestVo queryRequestVo = new QueryRequestVo();
        RDMPageVO rdmPageVO=new RDMPageVO();

        queryRequestVo.addCondition("bomLink.id", ConditionType.EQUAL, bomLinkId);

        rdmPageVO.setCurPage(1);
        rdmPageVO.setPageSize(1);

        List<BOMUsesOccurrenceViewDTO> bomUsesOccurrenceViewDTOS = bomUsesOccurrenceDelegator.find(queryRequestVo, rdmPageVO);

        return bomUsesOccurrenceViewDTOS.get(0).getReferenceDesignator();
    }

    /**
     * 更新BOM
     * @param bomUpdateDto
     */
    @Transactional
    public void update(BOMUpdateDto bomUpdateDto) {

        //更新BOMLink(数量关系)
        BOMLinkUpdateDTO bomLinkUpdateDTO = new BOMLinkUpdateDTO();
        bomLinkUpdateDTO.setQuantity(bomUpdateDto.getQuantity());
        bomLinkUpdateDTO.setId(bomUpdateDto.getBomLinkId());
        bomLinkDelegator.update(bomLinkUpdateDTO);

        //更新BOMUsesOccurrence(位号)
        Long id = getBOMUsesIdByBOMLink(bomUpdateDto.getBomLinkId());

        BOMUsesOccurrenceUpdateDTO bomUsesOccurrenceUpdateDTO = new BOMUsesOccurrenceUpdateDTO();
        bomUsesOccurrenceUpdateDTO.setId(id);
        bomUsesOccurrenceUpdateDTO.setReferenceDesignator(bomUpdateDto.getReferenceDesignator());
        bomUsesOccurrenceDelegator.update(bomUsesOccurrenceUpdateDTO);
        return;
    }

    @Transactional
    public void delete(List<Long> ids) {
        List<Long> bomUsesIds = new ArrayList<>();
        for(Long id: ids){
            Long bomUsesIdByBOMLink = getBOMUsesIdByBOMLink(id);
            bomUsesIds.add(bomUsesIdByBOMLink);
        }

        //批量删除BOMUsesOccurrence
        PersistObjectIdsModifierDTO persistObjectIdsModifierDTO1 = new PersistObjectIdsModifierDTO();
        persistObjectIdsModifierDTO1.setIds(bomUsesIds);
        bomUsesOccurrenceDelegator.batchDelete(persistObjectIdsModifierDTO1);

        //批量删除BOMLink
        PersistObjectIdsModifierDTO persistObjectIdsModifierDTO2 = new PersistObjectIdsModifierDTO();
        persistObjectIdsModifierDTO2.setIds(ids);
        bomLinkDelegator.batchDelete(persistObjectIdsModifierDTO2);
    }

    /**
     * 根据BOMLink查询BOMUsesOccurrence
     * @param bomLinkId
     * @return
     */
    public Long getBOMUsesIdByBOMLink(Long bomLinkId){
        QueryRequestVo queryRequestVo = new QueryRequestVo();
        RDMPageVO rdmPageVO=new RDMPageVO();

        queryRequestVo.addCondition("bomLink.id", ConditionType.EQUAL, bomLinkId);

        rdmPageVO.setCurPage(1);
        rdmPageVO.setPageSize(1);

        List<BOMUsesOccurrenceViewDTO> bomUsesOccurrenceViewDTOS = bomUsesOccurrenceDelegator.find(queryRequestVo, rdmPageVO);

        return bomUsesOccurrenceViewDTOS.get(0).getId();
    }


}
