package com.idme.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.huawei.innovation.rdm.coresdk.basic.enums.ConditionType;
import com.huawei.innovation.rdm.coresdk.basic.vo.QueryRequestVo;
import com.huawei.innovation.rdm.coresdk.basic.vo.RDMPageVO;
import com.huawei.innovation.rdm.coresdk.extattrmgmt.dto.EXAValueParamDTO;
import com.huawei.innovation.rdm.minibom.delegator.PartDelegator;
import com.huawei.innovation.rdm.minibom.dto.entity.PartBranchCreateDTO;
import com.huawei.innovation.rdm.minibom.dto.entity.PartCreateDTO;
import com.huawei.innovation.rdm.minibom.dto.entity.PartMasterCreateDTO;
import com.huawei.innovation.rdm.minibom.dto.entity.PartQueryViewDTO;
import com.huawei.innovation.rdm.xdm.bean.entity.ClassificationNode;
import com.huawei.innovation.rdm.xdm.delegator.ClassificationNodeDelegator;
import com.huawei.innovation.rdm.xdm.dto.entity.ClassificationNodeViewDTO;
import com.idme.pojo.dto.CategoryCreateDto;
import com.idme.pojo.dto.CategoryQueryDto;
import com.idme.pojo.dto.PartBuildDto;
import com.idme.service.CategoryService;
import com.idme.service.PartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 查询
     * @return
     */
    public List<PartQueryViewDTO> query() {
        QueryRequestVo queryRequestVo = new QueryRequestVo();
        RDMPageVO rdmPageVO = new RDMPageVO();
        return partDelegator.query(queryRequestVo, rdmPageVO);
    }
}