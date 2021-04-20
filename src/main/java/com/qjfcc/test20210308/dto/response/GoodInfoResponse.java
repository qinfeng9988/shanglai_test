package com.qjfcc.test20210308.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoodInfoResponse implements Serializable {
    private Integer id;
    private Double goods_price;
    private String goods_name;
    private String status;
}
