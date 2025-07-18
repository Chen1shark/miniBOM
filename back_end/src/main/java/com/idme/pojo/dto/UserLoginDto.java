package com.idme.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDto implements Serializable {
    //用户id
    private Long id;

    //用户名
    private String name;

    //jwt令牌
    private String token;
}
