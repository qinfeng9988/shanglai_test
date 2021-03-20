package com.qjfcc.test20210308.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShangLaiBaseResponse<T> {
    private Integer status;
    private String message;
    private T data;
    private T list;
    private Integer count;

}
