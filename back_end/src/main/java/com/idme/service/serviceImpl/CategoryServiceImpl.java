package com.idme.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huawei.iit.sdk.common.exception.RDMAppException;
import com.huawei.innovation.rdm.coresdk.basic.dto.GenericLinkTypeDTO;
import com.huawei.innovation.rdm.coresdk.basic.dto.PersistObjectIdModifierDTO;
import com.huawei.innovation.rdm.coresdk.basic.dto.QueryChildListDTO;
import com.huawei.innovation.rdm.coresdk.basic.enums.ConditionType;
import com.huawei.innovation.rdm.coresdk.basic.vo.QueryRequestVo;
import com.huawei.innovation.rdm.coresdk.basic.vo.RDMPageVO;
import com.huawei.innovation.rdm.minibom.dto.entity.UserViewDTO;
import com.huawei.innovation.rdm.xdm.bean.entity.ClassificationNode;
import com.huawei.innovation.rdm.xdm.delegator.*;
import com.huawei.innovation.rdm.xdm.dto.entity.*;
import com.huawei.innovation.rdm.xdm.dto.relation.EXADefinitionLinkCreateDTO;
import com.huawei.innovation.rdm.xdm.dto.relation.EXADefinitionLinkViewDTO;
import com.idme.constant.CategoryConstant;
import com.idme.constant.JwtClaimsConstant;
import com.idme.constant.MessageConstant;
import com.idme.context.BaseContext;
import com.idme.exception.CategoryNotFoundException;
import com.idme.pojo.dto.AttributeIdDto;
import com.idme.pojo.dto.CategoryQueryDto;
import com.idme.pojo.dto.CategoryUpdateDto;
import com.idme.pojo.vo.AttributeVO;
import com.idme.pojo.vo.CategorySimpleVO;
import com.idme.pojo.vo.CategoryVO;
import com.idme.properties.JwtProperties;
import com.idme.result.PageResult;
import com.idme.result.Result;
import com.idme.service.CategoryService;
import com.idme.service.UserService;
import com.idme.utils.HttpClientUtil;
import com.idme.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import com.idme.pojo.dto.CategoryCreateDto;
import com.huawei.innovation.rdm.coresdk.basic.dto.ObjectReferenceParamDTO;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private ClassificationNodeDelegator classificationNodeDelegator;

    @Autowired
    private EXADefinitionLinkDelegator exaDefinitionLinkDelegator;

    @Override
    public PageResult queryCategories(CategoryQueryDto queryDto) {
        QueryRequestVo queryRequestVo = new QueryRequestVo();
        RDMPageVO rdmPageVO = new RDMPageVO();

        // 设置分页参数（默认页码1，每页10条）
        rdmPageVO.setCurPage(queryDto.getCurPage() != null ? queryDto.getCurPage() : 1);
        rdmPageVO.setPageSize(queryDto.getPageSize() != null ? queryDto.getPageSize() : 10);

        // 添加分类编码查询条件（精确匹配）
        if (StringUtils.hasText(queryDto.getCode())) {
            queryRequestVo.addCondition("businessCode", ConditionType.EQUAL, queryDto.getCode());
        }

        // 添加分类名称查询条件（模糊匹配）
        if (StringUtils.hasText(queryDto.getName())) {
            queryRequestVo.addCondition("name", ConditionType.LIKE, queryDto.getName());
        }

        // 添加描述中文名称查询条件（模糊匹配）
        if (StringUtils.hasText(queryDto.getDescription())) {
            queryRequestVo.addCondition("description", ConditionType.LIKE, queryDto.getDescription());
        }

        // 添加分类状态查询条件（精确匹配）
        if (StringUtils.hasText(queryDto.getStatus())) {
            queryRequestVo.addCondition("status", ConditionType.EQUAL, queryDto.getStatus());
        }

        long count = classificationNodeDelegator.count(queryRequestVo);

        // 调用底层API执行分页查询
        List<ClassificationNodeViewDTO> result = classificationNodeDelegator.find(queryRequestVo, rdmPageVO);

        if (result == null || result.isEmpty()){
            QueryRequestVo queryRequestVo1 = new QueryRequestVo();
            queryRequestVo1.addCondition("businessCode", ConditionType.EQUAL, queryDto.getName());
            List<ClassificationNodeViewDTO> result1 = classificationNodeDelegator.find(queryRequestVo1, new RDMPageVO(1,10));
            if(result1 != null && result1.size() > 0){
                List<CategoryVO> categoryVOs_tmp = convertToCategoryVO(result1);
                return PageResult.builder()
                        .total(1)
                        .records(categoryVOs_tmp)
                        .build();
            }
            else{
                return PageResult.builder()
                        .total(0)
                        .records(null)
                        .build();
            }
        }

        // 转换结果为VO对象
        List<CategoryVO> categoryVOs = convertToCategoryVO(result);

        // 封装分页结果（注意：实际总数需从RDMPageVO获取，此处简化处理）
        return PageResult.builder()
                .total(count)
                .records(categoryVOs)
                .build();
    }

    /**
     * DTO转换为VO（提取分类核心属性）
     */
    private List<CategoryVO> convertToCategoryVO(List<ClassificationNodeViewDTO> dtoList) {
        List<CategoryVO> voList = new ArrayList<>();
        if (dtoList != null && !dtoList.isEmpty()) {
            for (ClassificationNodeViewDTO dto : dtoList) {
                CategoryVO vo = new CategoryVO();
                BeanUtils.copyProperties(dto, vo);
                vo.setId(dto.getId());
                vo.setCode(dto.getBusinessCode());
                vo.setName(dto.getName());
                vo.setNameEn(dto.getNameEn());
                vo.setDescription(dto.getDescription());
                vo.setDescriptionEn(dto.getDescriptionEn());
                vo.setStatus(dto.getDisableFlag() == null ? true : !dto.getDisableFlag());
                vo.setParentId(dto.getParentNode() != null ? dto.getParentNode().getId() : null);

                voList.add(vo);
            }
        }
        return voList;
    }

    @Override
    public Long createCategory(CategoryCreateDto createDto) {
        // 1. 校验分类编码是否已存在
        checkCodeUnique(createDto.getCode());

        // 2. 构建XDM创建DTO
        ClassificationNodeCreateDTO xdmCreateDTO = buildXdmCreateDTO(createDto);

        // 3. 调用XDM创建API
        ClassificationNodeViewDTO result = classificationNodeDelegator.create(xdmCreateDTO);

// 如果有子分类，递归创建子分类
        if (createDto.getDetailList() != null && !createDto.getDetailList().isEmpty()) {
            for (CategoryCreateDto detail : createDto.getDetailList()) {
                ClassificationNodeCreateDTO detailXdmDTO = buildXdmCreateDTO(detail);
                checkCodeUnique(detail.getCode());
                // 正确地设置父节点引用
                ObjectReferenceParamDTO parentRef = new ObjectReferenceParamDTO();
                parentRef.setClazz("ClassificationNode");
                parentRef.setClassName("ClassificationNode");
                parentRef.setId(result.getId());
                detailXdmDTO.setParentNode(parentRef);

                classificationNodeDelegator.create(detailXdmDTO);
            }
        }

        // 4. 返回新建分类的ID
        return result.getId();
    }

    /**
     * 校验分类编码唯一性
     */
    private void checkCodeUnique(String code) {
        QueryRequestVo queryRequestVo = new QueryRequestVo();
        queryRequestVo.addCondition("businessCode", ConditionType.EQUAL, code);
        RDMPageVO rdmPageVO = new RDMPageVO();
        rdmPageVO.setCurPage(1);
        rdmPageVO.setPageSize(1);

        List<ClassificationNodeViewDTO> list = classificationNodeDelegator.find(queryRequestVo, rdmPageVO);
        if (list != null && !list.isEmpty()) {
            throw new RuntimeException("分类编码已存在");
        }
    }

    /**
     * 转换为XDM创建DTO
     */
    private ClassificationNodeCreateDTO buildXdmCreateDTO(CategoryCreateDto createDto) {
        ClassificationNodeCreateDTO xdmDTO = new ClassificationNodeCreateDTO();
        BeanUtils.copyProperties(createDto, xdmDTO);

        // 状态转换：status=true表示启用，对应XDM的disableFlag=false
        xdmDTO.setDisableFlag(!createDto.getStatus());

        // 父节点处理（使用ObjectReferenceParamDTO）
        if (createDto.getParentId() != null) {
            ObjectReferenceParamDTO parentRef = new ObjectReferenceParamDTO();
            parentRef.setId(createDto.getParentId());  // 设置父节点ID
            xdmDTO.setParentNode(parentRef);          // 使用setParentNode方法
        }

        return xdmDTO;
    }

    /**
     * 修改分类修改name、nameEn、description、descriptionEn
     * @param updateDto
     * @return
     */
    @Override
    public void updateCategory(CategoryUpdateDto updateDto) {
        // 1. 根据ID查询要更新的分类信息
        QueryRequestVo queryRequestVo = new QueryRequestVo();
        queryRequestVo.addCondition("id", ConditionType.EQUAL, updateDto.getId());
        RDMPageVO rdmPageVO = new RDMPageVO();
        rdmPageVO.setCurPage(1);
        rdmPageVO.setPageSize(1);

        List<ClassificationNodeViewDTO> list = classificationNodeDelegator.find(queryRequestVo, rdmPageVO);
        if (list.isEmpty()) {
            throw new RuntimeException("分类信息不存在");
        }

        ClassificationNodeViewDTO viewDTO = list.get(0);

        // 2. 创建更新DTO并设置需要更新的字段
        ClassificationNodeUpdateDTO update = new ClassificationNodeUpdateDTO();
        update.setId(updateDto.getId());
        if (StringUtils.hasText(updateDto.getName())) {
            update.setName(updateDto.getName());
        }
        if (StringUtils.hasText(updateDto.getNameEn())) {
            update.setNameEn(updateDto.getNameEn());
        }
        if (StringUtils.hasText(updateDto.getDescription())) {
            update.setDescription(updateDto.getDescription());
        }
        if (StringUtils.hasText(updateDto.getDescriptionEn())) {
            update.setDescriptionEn(updateDto.getDescriptionEn());
        }

        // 3. 调用更新方法
        classificationNodeDelegator.update(update);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        // 1. 检查分类是否存在
        QueryRequestVo queryRequestVo = new QueryRequestVo();
        queryRequestVo.addCondition("id", ConditionType.EQUAL, categoryId);
        RDMPageVO rdmPageVO = new RDMPageVO();
        rdmPageVO.setCurPage(1);
        rdmPageVO.setPageSize(1);

        List<ClassificationNodeViewDTO> list = classificationNodeDelegator.find(queryRequestVo, rdmPageVO);
        if (list.isEmpty()) {
            throw new RuntimeException("分类不存在，无法删除");
        }

        // 2. 查询该分类是否有子分类
        QueryChildListDTO queryChildListDTO = new QueryChildListDTO();
        queryChildListDTO.setParentId(categoryId); // 设置要查询的父节点ID

        RDMPageVO rdmPageVO1 = new RDMPageVO();

        List<ClassificationNodeQueryViewDTO> children = classificationNodeDelegator.getChildList(queryChildListDTO, rdmPageVO1);

        // 3. 判断是否存在子类
        if (children != null && !children.isEmpty()) {
            throw new RuntimeException("该分类下存在子分类，不允许删除");
        }

        // 4. 创建 PersistObjectIdModifierDTO 对象并设置 ID
        PersistObjectIdModifierDTO deleteDTO = new PersistObjectIdModifierDTO();
        deleteDTO.setId(categoryId); // 设置要删除的分类ID

        // 5. 使用 DTO 执行删除操作
        classificationNodeDelegator.delete(deleteDTO);
    }

    /**
     * 批量新增分类与多个目标ID的链接关系
     * @return 新增的链接视图列表
     */
    @Override
    public List<EXADefinitionLinkViewDTO> batchCreateLinks(AttributeVO attributeVO){

        if (attributeVO.getSourceIds() == null || attributeVO.getSourceIds().isEmpty()) {
            throw new RuntimeException("目标ID集合不能为空");
        }

        // 构建批量创建DTO列表
        ObjectReferenceParamDTO objectReferenceParamDTO = new ObjectReferenceParamDTO();
        objectReferenceParamDTO.setClazz("ClassificationNode");
        objectReferenceParamDTO.setId(attributeVO.getClassificationId());
        List<EXADefinitionLinkCreateDTO> createDTOList = new ArrayList<>();
        for (Long sourceId : attributeVO.getSourceIds()) {
            EXADefinitionLinkCreateDTO createDTO = new EXADefinitionLinkCreateDTO();
            ObjectReferenceParamDTO source = new ObjectReferenceParamDTO();
            source.setClazz("EXADefinition");
            source.setId(sourceId);
            createDTO.setSource(source);
            createDTO.setTarget(objectReferenceParamDTO);
            createDTOList.add(createDTO);
        }

        // 调用batchCreate方法批量新增
        return exaDefinitionLinkDelegator.batchCreate(createDTOList);
    }

    /**
     * 根据分类查询属性
     * @param linkId
     * @return
     */

    public List<Map<String, Object>> queryAttribute(String linkId) {
        String url = "https://dme.cn-north-4.huaweicloud.com/rdm_b49541bdd3de4658aab470544248649c_app/publicservices/rdm/basic/api/ClassificationNode/attribute/find/100/1";

        // 构建固定请求参数
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        params.put("sort", "DESC");
        params.put("orderBy", "lastUpdateTime");
        params.put("isNeedTotal", true);

        // 构建filter条件（固定linkId和type）
        Map<String, Object> filter = new HashMap<>();
        filter.put("joiner", "and");
        Map<String, Object> linkIdCondition = new HashMap<>();
        linkIdCondition.put("conditionName", "linkId");
        linkIdCondition.put("operator", "=");
        linkIdCondition.put("conditionValues", Collections.singletonList(linkId));

        Map<String, Object> typeCondition = new HashMap<>();
        typeCondition.put("conditionName", "type");
        typeCondition.put("operator", "<>");
        typeCondition.put("conditionValues", Collections.singletonList("CATEGORY"));

        filter.put("conditions", Arrays.asList(linkIdCondition, typeCondition));
        params.put("filter", filter);
        requestBody.put("params", params);

        // 构建请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Auth-Token", BaseContext.getCurrentToken());

        try {
            HttpClientUtil.HttpResponse response = HttpClientUtil.doPost4JsonWithHeaders(url, requestBody, headers);

            // 解析响应体为Map结构
            Map<String, Object> responseMap = JSON.parseObject(response.getBody(),
                    new TypeReference<Map<String, Object>>() {});

            // 直接返回data字段的列表（已为Map集合）
            return (List<Map<String, Object>>) responseMap.get("data");

        } catch (RuntimeException ex) {
            throw new CategoryNotFoundException(MessageConstant.CATEGORY_NOT_FOUND);
        }
    }


    public void deleteAttribute(AttributeIdDto attributeIdDto) {
        String url = "https://dme.cn-north-4.huaweicloud.com/rdm_b49541bdd3de4658aab470544248649c_app/publicservices/rdm/basic/api/ClassificationNode/attribute/remove";
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> params = new HashMap<>();

        //构建请求体
        params.put("linkIds", attributeIdDto.getIds());
        requestBody.put("params", params);

        //构建请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Auth-Token", BaseContext.getCurrentToken());

        HttpClientUtil.HttpResponse response = HttpClientUtil.doPost4JsonWithHeaders(url, requestBody, headers);


        return;


    }

}
