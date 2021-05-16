package com.qjfcc.test20210308.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Auther: qinjiangfeng
 * @Date: 2021/3/8 10:42
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseResponse<T> implements Serializable {
    private Integer status;
    private String message;
    private T data;
    private T list;

    public static BaseResponse<Boolean> success() {
        return new BaseResponse<Boolean>(0, "ok", true, null);
    }

    public static BaseResponse<Boolean> error() {
        return new BaseResponse<Boolean>(999, "fail", true, null);
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<T>(0, "ok", data, null);
    }

    public static <T> BaseResponse<T> executing() {
        return new BaseResponse<T>(1, "执行中，请稍候", null, null);
    }

}
