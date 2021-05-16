package com.qjfcc.test20210308.service;

import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

/**
 * @Author: qinjiangfeng
 * @Date: 2021/4/30 17:30
 * @Description:
 */
@Service
public class TerminateBean {
    @PreDestroy
    public void preDestroy() {
        System.out.println("正在下线中，请稍候.....");
    }
}
