package com.idme.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.idme.constant.JwtClaimsConstant;
import com.idme.pojo.dto.CategoryQueryDto;
import com.idme.pojo.dto.CategoryUpdateDto;
import com.idme.pojo.vo.AttributeVO;
import com.idme.pojo.vo.CategoryVO;
import com.idme.properties.JwtProperties;
import com.idme.result.PageResult;
import com.idme.result.Result;
import com.idme.service.CategoryService;
import com.idme.service.UserService;
import com.idme.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.idme.pojo.dto.CategoryCreateDto;
import com.huawei.innovation.rdm.coresdk.basic.dto.ObjectReferenceParamDTO;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private ClassificationNodeDelegator classificationNodeDelegator;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;

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


        // 调用底层API执行分页查询
        List<ClassificationNodeViewDTO> result = classificationNodeDelegator.find(queryRequestVo, rdmPageVO);

        if (result == null || result.isEmpty()){
            return PageResult.builder()
                    .total(0)
                    .records(null)
                    .build();
        }

        // 转换结果为VO对象
        List<CategoryVO> categoryVOs = convertToCategoryVO(result);

        // 封装分页结果（注意：实际总数需从RDMPageVO获取，此处简化处理）
        return PageResult.builder()
                .total(result.size())
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

    @Override
    public JSONObject getAttributes(CategoryQueryDto queryDto) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. 构建请求参数
        Map<String, Object> requestParams = new HashMap<>();
        Map<String, Object> params = new HashMap<>();

        // 设置排序条件
        params.put("sort", "DESC");
        params.put("orderBy", "lastUpdateTime");
        params.put("isNeedTotal", true);

        // 构建过滤条件
        Map<String, Object> filter = new HashMap<>();
        filter.put("joiner", "and");

        List<Map<String, Object>> conditions = new ArrayList<>();

        // 添加linkId条件
        Map<String, Object> linkIdCondition = new HashMap<>();
        linkIdCondition.put("conditionName", "linkId");
        linkIdCondition.put("operator", "=");
        linkIdCondition.put("conditionValues", new String[]{queryDto.getId().toString()});
        conditions.add(linkIdCondition);

        // 添加type条件（固定值）
        Map<String, Object> typeCondition = new HashMap<>();
        typeCondition.put("conditionName", "type");
        typeCondition.put("operator", "<>");
        typeCondition.put("conditionValues", new String[]{"CATEGORY"});
        conditions.add(typeCondition);

        filter.put("conditions", conditions);
        params.put("filter", filter);

        requestParams.put("params", params);

        // 2. 设置请求头
        UserViewDTO userViewDTO = new UserViewDTO();
        userViewDTO.setName("chenkaiyu");
        userViewDTO.setPsw("Chen-_12138");
        UserViewDTO userViewDTO1 = userService.login(userViewDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, userViewDTO1.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token.trim());

        // 3. 构建请求实体
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestParams, headers);

        // 4. 构建URL
        String url = String.format("https://dme.cn-north-4.huaweicloud.com/rdm_b49541bdd3de4658aab470544248649c_app/publicservices/rdm/basic/api/ClassificationNode/attribute/find/%d/%d",
                1, 10);

        // 5. 发送POST请求
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class);

        // 6. 解析响应
        if (response.getStatusCode() == HttpStatus.OK) {
            return JSON.parseObject(response.getBody());
        } else {
            throw new RuntimeException("调用华为云接口失败，状态码: " + response.getStatusCode());
        }

    }

    @Override
    public JSONObject batchDeleteExaDefinitionLinks(List<String> ids) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. 构建请求参数
        Map<String, Object> requestParams = new HashMap<>();
        Map<String, Object> params = new HashMap<>();

        params.put("ids", ids);
        requestParams.put("params", params);

        // 2. 设置请求头
        //登录成功后，生成jwt令牌
        UserViewDTO userViewDTO = new UserViewDTO();
        userViewDTO.setName("chenkaiyu");
        userViewDTO.setPsw("Chen-_12138");
        UserViewDTO userViewDTO1 = userService.login(userViewDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, userViewDTO1.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);
        headers.set("Accept", "application/json");
//        headers.set("X-Csrf-Token", csrfToken); // CSRF防护
//        headers.set("X-User-Id", userId); // 用户标识
        headers.set("X-Requested-With", "XMLHttpRequest"); // 标识AJAX请求

        // 3. 打印调试信息
        System.out.println("========== 删除请求信息 ==========");
        System.out.println("URL: https://dme.cn-north-4.huaweicloud.com/rdm_b49541bdd3de4658aab470544248649c_app/services/rdm/common/api/EXADefinitionLink/batchDelete");
        System.out.println("Headers: " + headers);
        System.out.println("Body: " + JSON.toJSONString(requestParams, true));

        // 4. 构建请求实体
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestParams, headers);

        // 5. 发送POST请求
        ResponseEntity<String> response = restTemplate.exchange(
                "https://dme.cn-north-4.huaweicloud.com/rdm_b49541bdd3de4658aab470544248649c_app/services/rdm/common/api/EXADefinitionLink/batchDelete",
                HttpMethod.POST,
                requestEntity,
                String.class);

        // 6. 处理响应
        System.out.println("响应状态码: " + response.getStatusCode());
        System.out.println("响应体: " + response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {
            return JSON.parseObject(response.getBody());
        } else {
            throw new RuntimeException("删除操作失败: " + response.getStatusCode() + " - " + response.getBody());
        }
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

    @Override
    public PageResult getAttributes1(CategoryQueryDto queryDto) {
        QueryRequestVo queryRequestVo = new QueryRequestVo();
        RDMPageVO rdmPageVO = new RDMPageVO();
        GenericLinkTypeDTO dto  = new GenericLinkTypeDTO();
        dto.setSourceId(queryDto.getId());
        dto.setTargetType("ClassificationNode");
        List list = exaDefinitionLinkDelegator.queryTarget(dto, rdmPageVO);
        return PageResult.builder()
                .total(list.size())
                .records(list)
                .build();

    }
}