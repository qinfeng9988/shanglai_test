package com.qjfcc.test20210308.controller;

import com.google.common.collect.Lists;
import com.qjfcc.test20210308.dto.BaseResponse;
import com.qjfcc.test20210308.service.CrawTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @Auther: qinjiangfeng
 * @Date: 2021/3/8 10:41
 * @Description:
 */
@RestController
@RequestMapping("/jsoup/")
public class JsoupController {
    @Autowired
    CrawTextService crawTextService;

    @GetMapping("query")
    public BaseResponse<Boolean> queryContent() throws IOException {

        crawTextService.getText(true, true, "http://pm.shanglai.art/index/auction_goods/auction_goods_list");

//        crawTextService.getProduct(Lists.newArrayList(80, 79, 793, 1082));

        return BaseResponse.success();
    }


}
