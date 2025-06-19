package com.idme.service.serviceImpl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.huawei.innovation.rdm.coresdk.basic.dto.VersionCheckInDTO;
import com.huawei.innovation.rdm.coresdk.basic.dto.VersionCheckOutDTO;
import com.huawei.innovation.rdm.coresdk.basic.dto.VersionMasterDTO;
import com.huawei.innovation.rdm.coresdk.basic.dto.VersionMasterModifierDTO;
import com.huawei.innovation.rdm.coresdk.basic.enums.ConditionType;
import com.huawei.innovation.rdm.coresdk.basic.vo.QueryCondition;
import com.huawei.innovation.rdm.coresdk.basic.vo.QueryRequestVo;
import com.huawei.innovation.rdm.coresdk.basic.vo.RDMPageVO;
import com.huawei.innovation.rdm.coresdk.extattrmgmt.dto.EXAValueParamDTO;
import com.huawei.innovation.rdm.minibom.delegator.PartDelegator;
import com.huawei.innovation.rdm.minibom.dto.entity.*;
import com.huawei.innovation.rdm.xdm.delegator.ClassificationNodeDelegator;
import com.idme.constant.AttributeConstant;
import com.idme.pojo.dto.PartBuildDto;
import com.idme.pojo.dto.PartUpdateDto;
import com.idme.pojo.vo.PartVO;
import com.idme.pojo.vo.PartVersionVO;
import com.idme.service.PartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;

@Service
public class PartServiceImpl implements PartService {

    @Autowired
    PartDelegator partDelegator;

    @Autowired
    private ClassificationNodeDelegator classificationNodeDelegator;

    public Long createPart(PartBuildDto partBuildDto) {
        //1.创建所需对象
        PartCreateDTO partCreateDTO = new PartCreateDTO();
        PartMasterCreateDTO partMasterCreateDTO = new PartMasterCreateDTO();
        PartBranchCreateDTO partBranchCreateDTO = new PartBranchCreateDTO();
        List<EXAValueParamDTO> exaAttrs = new ArrayList<>();

        //2将dto对象映射到华为云对象()

        //2.1.写入主属性
        partCreateDTO.setMaster(partMasterCreateDTO);
        partCreateDTO.setBranch(partBranchCreateDTO);
        partCreateDTO.setPartType(partBuildDto.getPartType());
        partCreateDTO.setSource(partBuildDto.getSource());
        partCreateDTO.setName(partBuildDto.getName());

        //2.2.写入扩展属性
        exaAttrs.add(new EXAValueParamDTO("Number", partBuildDto.getNumber()));
        exaAttrs.add(new EXAValueParamDTO("Classification", partBuildDto.getCategoryId()));
        partCreateDTO.setExtAttrs(exaAttrs);

        //3.写入分类属性
        JSONArray jsonClsAttrs = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("Classification", new JSONObject(partBuildDto.getClsAttrs()));
        jsonClsAttrs.add(item);//转化为JSONArray

        partCreateDTO.setClsAttrs(jsonClsAttrs);

        //4.创建Part
        return partDelegator.create(partCreateDTO).getId();
    }

    /**
     * 更新
     *
     * @param partUpdateDto
     * @return version
     */
    public Long updatePart(PartUpdateDto partUpdateDto) {

        //1.根据MasterId检出
        Long partId = checkOutPart(partUpdateDto.getMasterId());
        checkInPart(partUpdateDto.getMasterId());

        //1.创建所需对象
        PartUpdateByAdminDTO partUpdateDTO = new PartUpdateByAdminDTO();
        List<EXAValueParamDTO> exaAttrs = new ArrayList<>();

        //2将dto对象映射到华为云对象()

        //2.1.写入主属性
        partUpdateDTO.setId(partId);
        partUpdateDTO.setPartType(partUpdateDto.getPartType());
        partUpdateDTO.setSource(partUpdateDto.getSource());
        partUpdateDTO.setName(partUpdateDto.getName());

        //2.2.写入扩展属性
        exaAttrs.add(new EXAValueParamDTO("Number", partUpdateDto.getNumber()));
        exaAttrs.add(new EXAValueParamDTO("Classification", partUpdateDto.getCategoryId()));
        partUpdateDTO.setExtAttrs(exaAttrs);

        //3.写入分类属性
        JSONArray jsonClsAttrs = new JSONArray();
        JSONObject item = new JSONObject();
        item.put("Classification", new JSONObject(partUpdateDto.getClsAttrs()));
        jsonClsAttrs.add(item);//转化为JSONArray

        partUpdateDTO.setClsAttrs(jsonClsAttrs);

        //4.更新Part
        partDelegator.updateByAdmin(partUpdateDTO);


        return partId;
    }



    /**
     * 查询
     * @return id
     */
    public List<PartVO> query(String searchType, String searchText, Integer pageSize, Integer curPage, AtomicLong count, Boolean isFilterOld) {
        //1.获取原始Part数据
        QueryRequestVo queryRequestVo = new QueryRequestVo();
        List<QueryCondition> queryConditions = new ArrayList<>();
        //1.1添加查询条件
        if (StringUtils.hasText(searchText)) {
            if(searchType.equals("name")){
                queryConditions.add(new QueryCondition("name", "like", searchText));
            } else if (searchType.equals("id")) {
                queryConditions.add(new QueryCondition("id", "=", searchText));
            }
        }
        if (isFilterOld) {
            queryConditions.add(new QueryCondition("latestIteration", "=", "true"));
        }
        queryRequestVo.setConditions(queryConditions);
        RDMPageVO rdmPageVO = new RDMPageVO();
        rdmPageVO.setCurPage(curPage);
        rdmPageVO.setPageSize(pageSize);
        List<PartViewDTO> partViewDTOs = partDelegator.find(queryRequestVo, rdmPageVO);

        //2.转化为PartVO
        List<PartVO> partVOS = new ArrayList<>();
        for(PartViewDTO dto : partViewDTOs){
            PartVO item = new PartVO();
            item.setPartId(dto.getId());
            item.setName(dto.getName());
            item.setVersion(dto.getVersion() + "." + dto.getIteration());
            item.setPartType(dto.getPartType());
            item.setSource(dto.getSource());
            item.setPartMasterId(dto.getMaster().getId());
            item.setParBranchId(dto.getBranch().getId());

            Object rawValue = dto.getExtAttrs().get(1).getValue();
            if (!(rawValue instanceof Map)) {
                rawValue = dto.getExtAttrs().get(0).getValue();
            }
            item.setBusinessCode((String) ((Map<String, Object>) rawValue).get("businessCode"));

            partVOS.add(item);
        }

        //3.计算总数
        count.set(partDelegator.count(queryRequestVo));

        return partVOS;
    }


    /**
     * 检出
     * @param MasterId
     * @return 检出后的对象od
     */
    public Long checkOutPart(Long MasterId) {
        VersionCheckOutDTO versionCheckOutDTO = new VersionCheckOutDTO();
        versionCheckOutDTO.setMasterId(MasterId);
        return partDelegator.checkout(versionCheckOutDTO).getId();
    }

    /**
     * 检入
     *
     * @param masterId
     */
    public void checkInPart(Long masterId) {
        VersionCheckInDTO versionCheckInDTO = new VersionCheckInDTO();
        versionCheckInDTO.setMasterId(masterId);
        partDelegator.checkin(versionCheckInDTO);
    }

    public List<PartVersionVO> versionQuery(Long masterId, Integer pageSize, Integer curPage) {
        VersionMasterDTO versionMasterDTO = new VersionMasterDTO();
        versionMasterDTO.setMasterId(masterId);

        RDMPageVO rdmPageVO = new RDMPageVO();
        rdmPageVO.setCurPage(curPage);
        rdmPageVO.setPageSize(pageSize);

        List<PartQueryViewDTO> partQueryViewDTOS = partDelegator.getAllVersions(versionMasterDTO, rdmPageVO);

        //转化为PartVO
        List<PartVersionVO> partVersionVOS = new ArrayList<>();
        for(PartQueryViewDTO dto : partQueryViewDTOS){
            PartVersionVO item = new PartVersionVO();
            item.setPartId(dto.getId());
            item.setName(dto.getName());
            item.setVersion(dto.getVersion() + "." + dto.getIteration());
            item.setLatestIteration(dto.getLatestIteration());
            partVersionVOS.add(item);
        }

         return partVersionVOS;
    }

    public void deleteByMasterId(Long masterId) {
        VersionMasterModifierDTO versionMasterModifierDTO = new VersionMasterModifierDTO();
        versionMasterModifierDTO.setMasterId(masterId);
        partDelegator.deleteLatestVersion(versionMasterModifierDTO);
    }

}