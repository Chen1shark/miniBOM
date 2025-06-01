package com.idme.service.serviceImpl;

import com.huawei.innovation.rdm.coresdk.basic.enums.ConditionType;
import com.huawei.innovation.rdm.coresdk.basic.vo.QueryRequestVo;
import com.huawei.innovation.rdm.coresdk.basic.vo.RDMPageVO;
import com.huawei.innovation.rdm.minibom.delegator.UserDelegator;
import com.huawei.innovation.rdm.minibom.dto.entity.UserCreateDTO;
import com.huawei.innovation.rdm.minibom.dto.entity.UserViewDTO;
import com.idme.exception.AccountNotFoundException;
import com.idme.exception.DuplicateUsernameException;
import com.idme.exception.PasswordEditFailedException;
import com.idme.service.UserService;
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
        queryRequestVo.addCondition("name",ConditionType.EQUAL,userViewDTO.getName());
        RDMPageVO rdmPageVO=new RDMPageVO();
        List<UserViewDTO> list= userDelegator.find(queryRequestVo,rdmPageVO);
        if(list.isEmpty()){
            throw new AccountNotFoundException("用户不存在");
        }else {
            userViewDTO.setPsw(DigestUtils.md5DigestAsHex(userViewDTO.getPsw().getBytes()));
            if(Objects.equals(list.get(0).getPsw(), userViewDTO.getPsw())){
                return list.get(0);
            }else
                throw new PasswordEditFailedException("密码错误");

        }

    }

    /**
     * 用户注册
     * @param userCreateDTO
     */
    public void register(UserCreateDTO userCreateDTO) {

        QueryRequestVo queryRequestVo=new QueryRequestVo();
        queryRequestVo.addCondition("name",ConditionType.EQUAL,userCreateDTO.getName());
        RDMPageVO rdmPageVO=new RDMPageVO();
        List<UserViewDTO> list= userDelegator.find(queryRequestVo,rdmPageVO);
        if(list != null) {
            throw new DuplicateUsernameException("用户名重复");
        }
        userCreateDTO.setPsw(DigestUtils.md5DigestAsHex(userCreateDTO.getPsw().getBytes()));
        userDelegator.create(userCreateDTO);
    }
}
