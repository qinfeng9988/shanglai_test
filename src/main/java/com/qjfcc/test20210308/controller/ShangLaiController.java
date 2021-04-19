package com.qjfcc.test20210308.controller;

import com.google.common.collect.Lists;
import com.qjfcc.test20210308.common.HttpClientUtil;
import com.qjfcc.test20210308.common.ThreadPoolUtil;
import com.qjfcc.test20210308.dto.*;
import com.qjfcc.test20210308.dto.request.ShangLaiStartRequest;
import com.qjfcc.test20210308.response.GoodInfoResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Auther: qinjiangfeng
 * @Date: 2021/3/8 10:49
 * @Description:
 */
@RestController
@RequestMapping("/shanglai/")
public class ShangLaiController {

    private Map<String, List<GoodInfoResponse>> maps = new HashMap<>();

    @PostMapping("start")
    public BaseResponse<Boolean> start(@RequestBody ShangLaiStartRequest request) {
        TimeIntervalEnum timeInterval = TimeIntervalEnum.convert(request.getTimeInterval());
        request.getProductId().forEach(pid -> {
            ThreadPoolUtil.execute(() -> {
                String threadName = String.format("thread-%s", pid);
                Thread.currentThread().setName(threadName);
                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                if (timeInterval != null && timeInterval.getStartTime() != null) {
                    try {
                        Date now = new Date();
                        long startSecond = timeInterval.getStartTime().getTime();
                        if (now.before(timeInterval.getStartTime())) {
                            long nowSecond = now.getTime();
                            System.out.println("[" + threadName + "] " + simpleFormat.format(now) + ",已预约时间段：" + simpleFormat.format(timeInterval.getStartTime()) + "," + (startSecond - nowSecond) + "毫秒后开始运行");
                            Thread.sleep(startSecond - nowSecond);
                        }
                    } catch (Exception ignored) {

                    }

                }

                System.out.println("[" + threadName + "] " + "抢购开始" + simpleFormat.format(new Date()));
                for (int i = 0; i < 150; i++) {
                    String body = String.format("{\"id\":%s,\"token\":\"" + request.getToken() + "\"}", pid);
                    RestTemplate restTemplate = new RestTemplate();
                    HttpHeaders header = new HttpHeaders();
                    header.setContentType(MediaType.APPLICATION_JSON);
                    header.setOrigin("http://pm.shanglai.art");
                    header.add("Referer", "http://pm.shanglai.art/vue/");
                    header.setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.ALL));
                    header.add("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Mobile Safari/537.36");
                    HttpEntity<String> httpEntity = new HttpEntity<>(body, header);

                    try {
                        String s = restTemplate.postForObject("http://pm.shanglai.art/index/auction_goods/buy_auction_goods", httpEntity, String.class);
                        if (StringUtils.isNotBlank(s)) {
                            if (s.contains("成功")) {
//                                hasProduct.set(true);
                                System.out.println("[" + threadName + "]" + " 抢购成功了");
                                break;
                            } else {
                                System.out.println("[" + threadName + "] " + simpleFormat.format(new Date()) + " " + s + pid);
                            }
                        }
                    } catch (Exception ignored) {

                    }
                }
                System.out.println("[" + threadName + "]" + "抢购结束" + simpleFormat.format(new Date()));
            });
        });
        return BaseResponse.success();
    }

    @PostMapping("start2")
    public BaseResponse<Boolean> start2(@RequestBody ShangLaiStartRequest request) {
        int threadCount = Optional.ofNullable(request.getThreadCount()).orElse(10);
        int count = Math.min(request.getProductId().size(), threadCount);
        int len = request.getProductId().size() / count;
        List<Integer> productIds = request.getProductId();
        TimeIntervalEnum timeInterval = TimeIntervalEnum.convert(request.getTimeInterval());

        int end, start;
        for (int i = 0; i < count; i++) {
            start = i * len;
            end = start + len;
            if (start == end) {
                break;
            }
            if (end > productIds.size()) {
                end = productIds.size();
            }
            splitStart(productIds.subList(start, end), request.getToken(), timeInterval, false);
        }

        return BaseResponse.success();
    }

    @PostMapping("vip/start")
    public BaseResponse<Boolean> vipStart(@RequestBody ShangLaiStartRequest request) {
        int threadCount = Optional.ofNullable(request.getThreadCount()).orElse(10);
        int count = Math.min(request.getProductId().size(), threadCount);
        int len = request.getProductId().size() / count;
        List<Integer> productIds = request.getProductId();
        TimeIntervalEnum timeInterval = TimeIntervalEnum.convert(request.getTimeInterval());

        int end, start;
        for (int i = 0; i < count; i++) {
            start = i * len;
            end = start + len;
            if (start == end) {
                break;
            }
            if (end > productIds.size()) {
                end = productIds.size();
            }
            splitStart(productIds.subList(start, end), request.getToken(), timeInterval, true);
        }

        return BaseResponse.success();
    }

    @GetMapping("download")
    public BaseResponse<List<GoodInfoResponse>> download(@RequestParam String
                                                                       token, @RequestParam Integer timeInterval, @RequestParam(required = false) Boolean retry) {

        TimeIntervalEnum timeIntervalType = Optional.ofNullable(TimeIntervalEnum.convert(timeInterval)).orElse(TimeIntervalEnum.MORNING);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = simpleDateFormat.format(new Date());
        String key = date + "~~" + timeIntervalType.getCode();
        if (maps.containsKey(key) && (retry == null || !retry)) {
            return BaseResponse.success(maps.get(key));
        }

        TimeIntervalEnum timeIntervalEnum = TimeIntervalEnum.convert(timeInterval);
        //String body = String.format("{\"tid\":%s,\"pageNo\":0,\"token\":\"%s\",\"pagesize\":100,\"visit\":\"1b\"}", timeIntervalEnum.getTid(), token);
        //tid=
        String body = String.format("tid=%s&pageNo=1&pageSize=10&visit=lb&token=%s", timeIntervalEnum.getTid(), token);
        String url = "http://pm.shanglai.art/index/fg/goods/goodsKillList?"+ body;
        HttpRequestEntity requestEntity = HttpRequestEntity.builder()
                .body(body)
                .origin("http://pm.shanglai.art")
                .referer("http://pm.shanglai.art/vue/")
                .url(url)
                .build();
        ShangLaiBaseResponse<QueryListPageResponse> s = HttpClientUtil.request(requestEntity, new ParameterizedTypeReference<ShangLaiBaseResponse<QueryListPageResponse>>() {
        });

        if (s.getResult()==null || CollectionUtils.isEmpty(s.getResult().getGoodsList()) || s.getResult().getPageController().getTotalPages() < 1) {
            return BaseResponse.success(null);
        }
        List<GoodInfoResponse> list = s.getResult().getGoodsList();
        for (int i = 2; i < s.getCount(); i++) {
            body = String.format("{\"tid\":%s,\"page\":%s,\"token\":\"%s\"}", timeIntervalType.getTid(), i, token);
            requestEntity = HttpRequestEntity.builder()
                    .body(body)
                    .origin("http://pm.shanglai.art")
                    .referer("http://pm.shanglai.art/vue/")
                    .url("http://pm.shanglai.art/index/auction_goods/auction_goods_list")
                    .build();
            s = HttpClientUtil.request(requestEntity, new ParameterizedTypeReference<ShangLaiBaseResponse<QueryListPageResponse>>() {
            });
            if (CollectionUtils.isEmpty(list)) {
                break;
            }
            list.addAll(s.getResult().getGoodsList());
        }
        list.sort((o1, o2) -> o2.getGoods_price().compareTo(o1.getGoods_price()));
        maps.put(key, list);
        return BaseResponse.success(list);
    }

    private void splitStart(List<Integer> productIds, String token, TimeIntervalEnum timeInterval,
                            boolean isVip) {
        ThreadPoolUtil.execute(() -> {
            SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            if (timeInterval != null) {
                try {
                    Date startTime = isVip ? timeInterval.getVipStartTime() : timeInterval.getStartTime();

                    Date now = new Date();
                    long startSecond = startTime.getTime();
                    if (now.before(startTime)) {
                        long nowSecond = now.getTime();
                        System.out.println(simpleFormat.format(now) + ",已预约时间段：" + simpleFormat.format(startTime) + "," + (startSecond - nowSecond) + "毫秒后开始运行");
                        Thread.sleep(startSecond - nowSecond);
                    }

                } catch (Exception ignored) {


                }
            }
            System.out.println("抢购开始" + simpleFormat.format(new Date()));
            Thread.currentThread().setName("thread-商品-[" + StringUtils.join(productIds, ",") + "]");
            AtomicBoolean hasProduct = new AtomicBoolean(false);
            for (int i = 0; i < 300; i++) {
                productIds.forEach(productId -> {
                    if (hasProduct.get()) {
                        System.out.println("已拍到产品，无需重拍");
                        return;
                    }
                    String body = String.format("{\"id\":%s,\"token\":\"" + token + "\"}", productId);

                    try {
                        HttpRequestEntity requestEntity = HttpRequestEntity.builder()
                                .body(body)
                                .origin("http://pm.shanglai.art")
                                .referer("http://pm.shanglai.art/vue/")
                                .url("http://pm.shanglai.art/index/auction_goods/buy_auction_goods")
                                .build();
                        String s = HttpClientUtil.request(requestEntity, new ParameterizedTypeReference<String>() {
                        });
                        if (StringUtils.isNotBlank(s)) {
                            if (s.contains("成功")) {
                                hasProduct.set(true);
                                System.out.println("thread-商品-" + productId + "抢购成功了");
                            } else {
                                System.out.println(simpleFormat.format(new Date()) + " " + s);
                            }
                        }
                    } catch (Exception ignored) {

                    }
                });
                if (i % 10 == 0) {
                    System.out.println(Thread.currentThread().getName() + "正在抢拍，不要着急," + i);
                }
                if (hasProduct.get()) {
                    break;
                }
            }
            System.out.println("抢购结束" + simpleFormat.format(new Date()));
        });
    }

}

