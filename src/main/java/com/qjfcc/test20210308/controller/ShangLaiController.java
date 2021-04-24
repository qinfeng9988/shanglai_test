package com.qjfcc.test20210308.controller;

import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.qjfcc.test20210308.common.HttpClientUtil;
import com.qjfcc.test20210308.common.ThreadPoolUtil;
import com.qjfcc.test20210308.dto.*;
import com.qjfcc.test20210308.dto.request.ShangLaiStartRequest;
import com.qjfcc.test20210308.dto.response.GoodInfoResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: qinjiangfeng
 * @Date: 2021/3/8 10:49
 * @Description:
 */
@RestController
@RequestMapping("/shanglai/")
public class ShangLaiController {

    private static final int count = 300;

    private Map<String, List<GoodInfoResponse>> maps = new HashMap<>();

    /**
     * 并行抢拍
     * @param request
     * @return
     */
    @PostMapping("concurrent/start")
    public BaseResponse<Boolean> start(@RequestBody ShangLaiStartRequest request) {
        if (CollectionUtils.isEmpty(request.getProductId())) {
            return BaseResponse.error();
        }
        TimeIntervalEnum timeInterval = TimeIntervalEnum.convert(request.getTimeInterval());
        CyclicBarrier cyclicBarrierStart = new CyclicBarrier(request.getProductId().size(), () -> {
            System.out.println("一轮的开始");
        });
        CyclicBarrier cyclicBarrierEnd = new CyclicBarrier(request.getProductId().size(), () -> {
            System.out.println("一轮的结束");
        });
        request.getProductId().forEach(pid -> {
            ThreadPoolUtil.execute(() -> {
                SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                if (timeInterval != null && timeInterval.getStartTime() != null) {
                    try {
                        Date now = new Date();
                        long startSecond = timeInterval.getStartTime().getTime();
                        if (now.before(timeInterval.getStartTime())) {
                            long nowSecond = now.getTime();
                            System.out.println(simpleFormat.format(now) + ",已预约时间段：" + simpleFormat.format(timeInterval.getStartTime()) + "," + (startSecond - nowSecond) + "毫秒后开始运行");
                            Thread.sleep(startSecond - nowSecond);
                        }
                    } catch (Exception ignored) {

                    }

                }
                AtomicBoolean hasProduct = new AtomicBoolean(false);

                System.out.println("抢购开始" + simpleFormat.format(new Date()) + ",[" + pid + "]");
                for (int i = 0; i < count; i++) {
                    try {
                        cyclicBarrierStart.await();
                    } catch (Exception ignored) {
                    }
                    if (!hasProduct.get()) {
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
                                    hasProduct.set(true);
                                    System.out.println("thread-商品-" + pid + "抢购成功了");
                                } else {
                                    System.out.println(simpleFormat.format(new Date()) + " " + s + pid);
                                }
                            }

                        } catch (Exception ignored) {

                        }
                    }
                    try {
                        cyclicBarrierEnd.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("抢购结束" + simpleFormat.format(new Date()) + ",[" + pid + "]");
            });

        });
        return BaseResponse.success();
    }

    /**
     * 并发抢拍
     * @param request
     * @return
     */
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
            splitStart(productIds.subList(start, end), request.getToken(), timeInterval, false, request.getName());
        }

        return BaseResponse.success();
    }

    @PostMapping("start3")
    public BaseResponse<Boolean> start3(@RequestBody ShangLaiStartRequest request) {
        int threadCount = 10;
        TimeIntervalEnum timeInterval = TimeIntervalEnum.convert(request.getTimeInterval());
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        CyclicBarrier cyclicBarrierStart = new CyclicBarrier(threadCount, () -> {
            System.out.println("一轮开始");
        });

        CyclicBarrier cyclicBarrierEnd = new CyclicBarrier(threadCount, () -> {
            System.out.println("一轮结束");
        });
        int whileCount = 10;
        for (int i = 0; i < threadCount; i++) {
            ThreadPoolUtil.execute(() -> {
                try {
                    for (int j = 0; j < whileCount; j++) {
                        cyclicBarrierStart.await();

                        System.out.println(simpleFormat.format(new Date()) + "开始干活" + j + "," + Thread.currentThread().getName());
                        Random r = new Random();
                        int s = r.nextInt(1000);
                        Thread.sleep(s);

                        cyclicBarrierEnd.await();
                    }
                } catch (Exception ignored) {

                }
            });
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
            splitStart(productIds.subList(start, end), request.getToken(), timeInterval, true, request.getName());
        }

        return BaseResponse.success();
    }

    @GetMapping("download")
    public BaseResponse<List<GoodInfoResponse>> download(@RequestParam String token, @RequestParam Integer timeInterval, @RequestParam(required = false) Boolean retry) {

        TimeIntervalEnum timeIntervalType = Optional.ofNullable(TimeIntervalEnum.convert(timeInterval)).orElse(TimeIntervalEnum.MORNING);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = simpleDateFormat.format(new Date());
        String key = date + "~~" + timeIntervalType.getCode();


        boolean fromCache = maps.containsKey(key) && (retry == null || !retry);
        if (fromCache) {
            return BaseResponse.success(maps.get(key));
        }

        TimeIntervalEnum timeIntervalEnum = TimeIntervalEnum.convert(timeInterval);
        String body = String.format("tid=%s&pageNo=1&pageSize=10&visit=lb&token=%s", timeIntervalEnum.getTid(), token);
        String url = "http://pm.shanglai.art/index/fg/goods/goodsKillList";
        HttpRequestEntity requestEntity = HttpRequestEntity.builder()
                .body(body)
                .origin("http://pm.shanglai.art")
                .referer("http://pm.shanglai.art/vue/")
                .url(url)
                .build();


        ShangLaiBaseResponse<QueryListPageResponse> s = HttpClientUtil.requestV2(requestEntity, new TypeReference<ShangLaiBaseResponse<QueryListPageResponse>>() {
        });
        QueryListPageResponse queryListPageResponse = s.getResult();
        if (s.getResult() == null || CollectionUtils.isEmpty(queryListPageResponse.getGoodsList()) || queryListPageResponse.getPageController().getTotalPages() < 1) {
            return BaseResponse.success(null);
        }
        List<GoodInfoResponse> list = queryListPageResponse.getGoodsList();
        for (int i = 2; i < s.getResult().getPageController().getTotalPages(); i++) {
            body = String.format("tid=%s&pageNo=%s&pageSize=10&visit=lb&token=%s", timeIntervalEnum.getTid(), i, token);
            requestEntity = HttpRequestEntity.builder()
                    .body(body)
                    .origin("http://pm.shanglai.art")
                    .referer("http://pm.shanglai.art/vue/")
                    .url(url)
                    .build();
            s = HttpClientUtil.requestV2(requestEntity, new TypeReference<ShangLaiBaseResponse<QueryListPageResponse>>() {
            });
            if (s == null || CollectionUtils.isEmpty(list) || CollectionUtils.isEmpty(s.getResult().getGoodsList())) {
                break;
            }
            list.addAll(s.getResult().getGoodsList());
        }
        list.sort((o1, o2) -> o2.getGoods_price().compareTo(o1.getGoods_price()));
        maps.put(key, list);
        return BaseResponse.success(list);
    }

    private void splitStart(List<Integer> productIds, String token, TimeIntervalEnum timeInterval,
                            boolean isVip, String userName) {
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        ThreadPoolUtil.execute(() -> {
            try {
                Date startTime = isVip ? timeInterval.getVipStartTime() : timeInterval.getStartTime();

                Date now = new Date();
                long startSecond = startTime.getTime();
                if (now.before(startTime)) {
                    long nowSecond = now.getTime();
                    System.out.println(simpleFormat.format(now) + ",已预约时间段：" + simpleFormat.format(startTime) + "," + (startSecond - nowSecond) + "毫秒后开始运行(" + userName + ")");
                    Thread.sleep(startSecond - nowSecond);
                }
            } catch (Exception ignored) {

            }
            System.out.println("抢购开始" + simpleFormat.format(new Date()));
            Thread.currentThread().setName("thread-商品-[" + StringUtils.join(productIds, ",") + "]");
            AtomicBoolean hasProduct = new AtomicBoolean(false);
            AtomicBoolean killFail = new AtomicBoolean(false);
            for (int i = 0; i < count; i++) {
                productIds.forEach(productId -> {
                    if (hasProduct.get()) {
                        System.out.println("已拍到产品，无需重拍");
                        return;
                    }
                    if (killFail.get()) {
                        System.out.println("已被其它用户拍走了");
                        return;
                    }
                    String body = String.format("ag_id=%s&token=%s&visit=%s&tid=%s", productId, token
                            , timeInterval.getVisit()
                            , timeInterval.getTid());
                    try {
                        HttpRequestEntity requestEntity = HttpRequestEntity.builder()
                                .body(body)
                                .origin("http://pm.shanglai.art")
                                .referer("http://pm.shanglai.art/vue/")
                                .url("http://pm.shanglai.art/index/fg/goods/goodsKillOrder")
                                .build();
                        String s = HttpClientUtil.requestV2(requestEntity, new TypeReference<String>() {
                        });
                        if (StringUtils.isNotBlank(s)) {
                            if (s.contains("成功")) {
                                hasProduct.set(true);
                                System.out.println("thread-商品-" + productId + "抢购成功了(" + userName + ")");
                            } else if (s.contains("商品已其他用户被拍下")) {
                                killFail.set(true);
                                System.out.println(simpleFormat.format(new Date()) + "-"+productId + "被其它人抢走了");
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
                if (killFail.get()) {
                    break;
                }
            }
            System.out.println("抢购结束" + simpleFormat.format(new Date()));
        });

    }

}

