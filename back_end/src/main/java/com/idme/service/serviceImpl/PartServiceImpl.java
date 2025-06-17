package com.idme.service.serviceImpl;

import com.huawei.innovation.rdm.minibom.bean.enumerate.AssemblyMode;
import com.huawei.innovation.rdm.minibom.bean.enumerate.PartSource;
import com.huawei.innovation.rdm.minibom.delegator.PartDelegator;
import com.huawei.innovation.rdm.minibom.dto.entity.PartBranchCreateDTO;
import com.huawei.innovation.rdm.minibom.dto.entity.PartCreateDTO;
import com.huawei.innovation.rdm.minibom.dto.entity.PartMasterCreateDTO;
import com.huawei.innovation.rdm.xdm.delegator.EXADefinitionDelegator;
import com.idme.pojo.dto.PartBuildDto;
import com.idme.service.PartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PartServiceImpl implements PartService {

    @Autowired
    PartDelegator partDelegator;

    public Long createPart(PartBuildDto partBuildDto) {
        PartCreateDTO partCreateDTO = new PartCreateDTO();
        PartMasterCreateDTO partMasterCreateDTO = new PartMasterCreateDTO();
        PartBranchCreateDTO partBranchCreateDTO = new PartBranchCreateDTO();
        partCreateDTO.setMaster(partMasterCreateDTO);
        partCreateDTO.setBranch(partBranchCreateDTO);
        partCreateDTO.setPartType(partBuildDto.getPartType());
        partCreateDTO.setSource(partBuildDto.getSource());
        partCreateDTO.setName(partBuildDto.getName());

        return partDelegator.create(partCreateDTO).getId();
    }
}