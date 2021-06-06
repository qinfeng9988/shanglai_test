package com.qjfcc.test20210308.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Auther: qinjiangfeng
 * @Date: 2021/3/8 10:50
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShangLaiStartRequest {
    private List<Integer> productId;
    private String token;
    private Integer threadCount;
    private Integer timeInterval;
    private String name;
    private Integer limitSeconds;
}
