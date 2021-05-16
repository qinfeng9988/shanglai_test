package com.qjfcc.test20210308.service;

import com.qjfcc.test20210308.common.ThreadPoolUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: qinjiangfeng
 * @Date: 2021/3/2 13:53
 * @Description:
 */
@Service
public class CrawTextService {
    private volatile boolean hasProduct = false;

    /***
     * 获取文本
     *
     * @param autoDownloadFile
     *            自动下载文件
     * @param Multithreading
     *            多线程 默认false
     * @param Url
     *            网站链接
     * @throws IOException
     */
    public void getText(boolean autoDownloadFile, boolean Multithreading, String Url) throws IOException {
        String rule = "abs:href";
        List<String> urlList = new ArrayList<>();

        String dataString = "{\"tid\":\"2\",\"page\":0,\"token\":\"3fb30877ac71378e5cd9111cf1c13eb3\"}";

        Document document = Jsoup.connect(Url)
                .timeout(4000)
                .ignoreContentType(true)
                .userAgent("Mozilla\" to \"Mozilla/5.0 (Windows NT 10.0; WOW64; rv:50.0)")
                .header("Content-Type", "application/json;charset=UTF-8")
                .requestBody(dataString)
                .post();

        System.out.println(document.toString());
        Elements urlNode = document.select("a[href$=.html]");

        for (Element element : urlNode) {
            urlList.add(element.attr(rule));
        }

        CrawTextThread crawTextThread = new CrawTextThread(urlList);
        crawTextThread.start();


    }


    public void getProduct(List<Integer> productIds) {
        for (Integer productId : productIds) {
            ThreadPoolUtil.execute(() -> {
                if (hasProduct) {
                    System.out.println("已拍到商品，可以停了");
                    return;
                }
                for (int i = 0; i < 10000; i++) {

                    if (i % 10 == 0) {
                        System.out.println("刷了10次了，还没有刷到" + i);
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String body = String.format("{\"id\":%d,\"token\":\"d895276fa5de58de0da1cf6d1eb05f67\"}", productId);
                    RestTemplate restTemplate = new RestTemplate();
                    HttpHeaders header = new HttpHeaders();
                    // 需求需要传参为form-data格式
                    header.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<String> httpEntity = new HttpEntity<>(body, header);

//        restTemplate.postForLocation("http://ss.shanglai.art/index/auction_goods/buy_auction_goods", "{\"id\":409,\"token\":\"d895276fa5de58de0da1cf6d1eb05f67\"}");
                    String s = restTemplate.postForObject("http://ss.shanglai.art/index/auction_goods/buy_auction_goods", httpEntity, String.class);
                    System.out.println(s);
                }

            });
        }


    }
}
