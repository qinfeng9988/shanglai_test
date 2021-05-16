package com.qjfcc.test20210308.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @Author: qinjiangfeng
 * @Date: 2021/5/12 12:48
 * @Description:
 */
@Data
public class OrderDetailResponse {
    private Integer id;
    private Integer ag_id;
    private String buy_mobile;
    private String buy_real_name;
    private String buy_nick_name;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone="GMT+8")
    private Date create_time;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone="GMT+8")
    private Date pay_time;
    private String sell_real_name;
    private String sell_nick_name;
    private String sell_mobile;
    private String goods_name;
    private String total_money;
    private Integer user_id;
}
