package com.idme.service.serviceImpl;

import com.huawei.innovation.rdm.coresdk.basic.enums.ConditionType;
import com.huawei.innovation.rdm.coresdk.basic.vo.QueryRequestVo;
import com.huawei.innovation.rdm.coresdk.basic.vo.RDMPageVO;
import com.huawei.innovation.rdm.minibom.delegator.UserDelegator;
import com.huawei.innovation.rdm.minibom.dto.entity.UserCreateDTO;
import com.huawei.innovation.rdm.minibom.dto.entity.UserViewDTO;
import com.idme.constant.MessageConstant;
import com.idme.constant.UserConstant;
import com.idme.exception.*;
import com.idme.service.UserService;
import com.idme.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;
import java.util.Objects;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    public UserDelegator userDelegator;

    /**
     * 用户登录
     * @param userViewDTO
     * @return
     */
    public UserViewDTO login(UserViewDTO userViewDTO) {
        QueryRequestVo queryRequestVo=new QueryRequestVo();
        queryRequestVo.addCondition(UserConstant.NAME,ConditionType.EQUAL,userViewDTO.getName());
        RDMPageVO rdmPageVO=new RDMPageVO();
        List<UserViewDTO> list= userDelegator.find(queryRequestVo,rdmPageVO);
        if(list == null){
            throw new AccountNotFoundException(MessageConstant.LOGIN_CREDENTIAL_ERROR);
        }else {
            userViewDTO.setPsw(DigestUtils.md5DigestAsHex(userViewDTO.getPsw().getBytes()));
            if(Objects.equals(list.get(0).getPsw(), userViewDTO.getPsw())){
                return list.get(0);
            }else
                throw new PasswordEditFailedException(MessageConstant.LOGIN_CREDENTIAL_ERROR);

        }

    }

    /**
     * 用户注册
     * @param userCreateDTO
     */
    public void register(UserCreateDTO userCreateDTO) {
        if(!ValidationUtils.isValidUsername(userCreateDTO.getName())){
            throw new UsernameFormatException(MessageConstant.USERNAME_FORMAT_ERROR);
        }
        if(!ValidationUtils.isValidPassword(userCreateDTO.getPsw())){
            throw new PasswordFormatException(MessageConstant.PASSWORD_FORMAT_ERROR);
        }

        QueryRequestVo queryRequestVo=new QueryRequestVo();
        queryRequestVo.addCondition(UserConstant.NAME,ConditionType.EQUAL,userCreateDTO.getName());
        RDMPageVO rdmPageVO=new RDMPageVO();
        List<UserViewDTO> list= userDelegator.find(queryRequestVo,rdmPageVO);
        if(list != null) {
            throw new DuplicateUsernameException(MessageConstant.USERNAME_DUPLICATE);
        }
        userCreateDTO.setPsw(DigestUtils.md5DigestAsHex(userCreateDTO.getPsw().getBytes()));
        userDelegator.create(userCreateDTO);
    }
}
