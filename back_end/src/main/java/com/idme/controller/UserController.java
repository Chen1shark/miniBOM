package com.idme.controller;



import com.huawei.innovation.rdm.minibom.dto.entity.UserCreateDTO;
import com.huawei.innovation.rdm.minibom.dto.entity.UserViewDTO;

import com.idme.constant.JwtClaimsConstant;
import com.idme.pojo.dto.UserLoginDto;
import com.idme.properties.JwtProperties;
import com.idme.result.Result;
import com.idme.service.UserService;
import com.idme.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * DelegatorController
 *
 * @since 2024-04-11
 */
@RequestMapping("/user")
@RestController
public class UserController {
    /**
     * 引入PersistableModel模型的Delegator代理类。
     */


    @Autowired
    private UserService userService;

    @Autowired
    private JwtProperties jwtProperties;


    /**
     * 用户登录
     * @param userViewDTO
     * @return
     */
    @PostMapping("/login")
    public Result<UserLoginDto> login(@RequestBody UserViewDTO userViewDTO) {
        UserViewDTO userViewDTO1 = userService.login(userViewDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, userViewDTO1.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        UserLoginDto userLoginDto = UserLoginDto.builder()
                .id(userViewDTO1.getId())
                .name(userViewDTO1.getName())
                .token(token)
                .build();

        return Result.success(userLoginDto);
    }

    @PostMapping("/register")
    public Result register(@RequestBody UserCreateDTO userCreateDTO){
        userService.register(userCreateDTO);
        return Result.success();
    }


}
