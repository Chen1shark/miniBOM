package com.idme.service;


import com.huawei.innovation.rdm.minibom.dto.entity.UserCreateDTO;
import com.huawei.innovation.rdm.minibom.dto.entity.UserViewDTO;

public interface UserService {
    /**
     * 用户登录
     * @param userViewDTO
     * @return
     */
    UserViewDTO login(UserViewDTO userViewDTO);

    /**
     * 用户注册
     * @param userCreateDTO
     */
    void register(UserCreateDTO userCreateDTO);
}
