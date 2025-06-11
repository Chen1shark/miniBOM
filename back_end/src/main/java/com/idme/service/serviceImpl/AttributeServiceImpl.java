package com.idme.service.serviceImpl;

import com.huawei.innovation.rdm.coresdk.basic.enums.ConditionType;
import com.huawei.innovation.rdm.coresdk.basic.vo.QueryRequestVo;
import com.huawei.innovation.rdm.coresdk.basic.vo.RDMPageVO;
import com.huawei.innovation.rdm.xdm.delegator.EXADefinitionDelegator;
import com.huawei.innovation.rdm.xdm.dto.entity.EXADefinitionViewDTO;
import com.idme.constant.AttributeConstant;
import com.idme.constant.MessageConstant;
import com.idme.exception.AccountNotFoundException;
import com.idme.exception.AttributeNotFoundException;
import com.idme.service.AttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

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
}
