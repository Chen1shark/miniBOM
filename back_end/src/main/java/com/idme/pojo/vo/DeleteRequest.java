package com.idme.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class DeleteRequest {
    private List<String> ids;
}
