package com.qjfcc.test20210308.common;

/**
 * @Author: qinjiangfeng
 * @Date: 2021/5/11 10:26
 * @Description:
 */
public class NumberUtil {
    public static int random(int start, int end) {
        double randDouble = Math.random() * start;
        return end - (int) randDouble;
    }
}
