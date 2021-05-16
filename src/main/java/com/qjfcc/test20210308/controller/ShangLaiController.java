package com.qjfcc.test20210308.controller;

import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.qjfcc.test20210308.common.*;
import com.qjfcc.test20210308.dto.*;
import com.qjfcc.test20210308.dto.request.ShangLaiStartRequest;
import com.qjfcc.test20210308.dto.response.GoodInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @Author: qinjiangfeng
 * @Date: 2021/3/8 10:49
 * @Description:
 */
@Slf4j
@RestController
@RequestMapping("/shanglai/")
public class ShangLaiController {

    private static final int count = 300;

    private Map<String, List<GoodInfoResponse>> maps = new HashMap<>();

    private Map<String, List<OrderDetailResponse>> analyesMaps = new HashMap<>();

    private Date lastUpdateTime = new Date();

    private String requestHost = "http://ss.shanglai.art";

    /**
     * 并行抢拍
     *
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
            log.info("一轮的开始");
        });
        CyclicBarrier cyclicBarrierEnd = new CyclicBarrier(request.getProductId().size(), () -> {
            log.info("一轮的结束");
        });
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
                            log.info(simpleFormat.format(now) + ",已预约时间段：" + simpleFormat.format(timeInterval.getStartTime()) + "," + (startSecond - nowSecond) + "毫秒后开始运行");
                            Thread.sleep(startSecond - nowSecond);
                        }
                    } catch (Exception ignored) {

                    }

                }
                AtomicBoolean hasProduct = new AtomicBoolean(false);

                log.info("抢购开始" + simpleFormat.format(new Date()) + ",[" + pid + "]");
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
                        header.setOrigin(requestHost);
                        header.add("Referer", requestHost + "/vue/");
                        header.setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.ALL));
                        header.add("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Mobile Safari/537.36");
                        HttpEntity<String> httpEntity = new HttpEntity<>(body, header);

                        try {
                            String s = restTemplate.postForObject(requestHost + "/index/auction_goods/buy_auction_goods", httpEntity, String.class);
                            if (StringUtils.isNotBlank(s)) {
                                if (s.contains("成功")) {
                                    hasProduct.set(true);
                                    log.info("thread-商品-" + pid + "抢购成功了");
                                } else {
                                    log.info(simpleFormat.format(new Date()) + " " + s + pid);
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
                log.info("抢购结束" + simpleFormat.format(new Date()) + ",[" + pid + "]");
            });

        });
        return BaseResponse.success();
    }

    /**
     * 并发抢拍
     *
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

    @GetMapping("start3")
    public BaseResponse<Boolean> start3() {
        for (int i = 0; i < 10; i++) {
            System.out.println(NumberUtil.random(1000, 3000));
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
    public BaseResponse<List<GoodInfoResponse>> download(@RequestParam String token
            , @RequestParam Integer timeInterval
            , @RequestParam(required = false) Boolean retry
            , @RequestParam(required = false) String status) {

        TimeIntervalEnum timeIntervalType = Optional.ofNullable(TimeIntervalEnum.convert(timeInterval)).orElse(TimeIntervalEnum.MORNING);
        Date date = new Date();
        Date lastDate = new DateTime(date).plusSeconds(86400).toDate();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateString = simpleDateFormat.format(new Date());
        String lastDateString = simpleDateFormat.format(lastDate);
        String key = dateString + "~~" + timeIntervalType.getCode();


        boolean fromCache = maps.containsKey(key) && (retry == null || !retry);
        if (fromCache) {
            List<GoodInfoResponse> goodInfoResponseList = maps.get(key);
            if (StringUtils.isNotBlank(status)) {
                goodInfoResponseList = goodInfoResponseList.stream().filter(g -> status.equals(g.getStatus())).collect(Collectors.toList());
            }
            return BaseResponse.success(goodInfoResponseList);
        }

        TimeIntervalEnum timeIntervalEnum = TimeIntervalEnum.convert(timeInterval);
        Map<String, String> paramMaps = new HashMap<>();
        paramMaps.put("pageNo", String.valueOf(1));
        paramMaps.put("token", token);
        paramMaps.put("pageSize", String.valueOf(10));
        paramMaps.put("visit", timeIntervalEnum.getVisit());
        paramMaps.put("tid", String.valueOf(timeIntervalEnum.getTid()));
        String body = StringHelper.parameterJoin(paramMaps);

        String url = requestHost + "/index/fg/goods/goodsKillList";
        HttpRequestEntity requestEntity = HttpRequestEntity.builder()
                .body(body)
                .origin(requestHost)
                .referer(requestHost + "/vue/")
                .url(url)
                .build();
        ShangLaiBaseResponse<QueryListPageResponse> s = HttpClientUtil.requestV2(requestEntity, new TypeReference<ShangLaiBaseResponse<QueryListPageResponse>>() {
        });
        QueryListPageResponse queryListPageResponse = s.getResult();
        paramMaps.clear();
        if (s.getResult() == null || CollectionUtils.isEmpty(queryListPageResponse.getGoodsList()) || queryListPageResponse.getPageController().getTotalPages() < 1) {
            return BaseResponse.success(null);
        }
        ThreadPoolUtil.execute(() -> {
            List<GoodInfoResponse> list = queryListPageResponse.getGoodsList();
            for (int i = 2; i < s.getResult().getPageController().getTotalPages(); i++) {
//                try {
//                    Thread.sleep(NumberUtil.random(1000, 3000));
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                paramMaps.put("pageNo", String.valueOf(i));
                paramMaps.put("token", token);
                paramMaps.put("pageSize", String.valueOf(10));
                paramMaps.put("visit", timeIntervalEnum.getVisit());
                paramMaps.put("tid", String.valueOf(timeIntervalEnum.getTid()));

                String requestBody = StringHelper.parameterJoin(paramMaps);
                HttpRequestEntity request = HttpRequestEntity.builder()
                        .body(requestBody)
                        .origin(requestHost)
                        .referer(requestHost + "/vue/")
                        .url(url)
                        .build();
                ShangLaiBaseResponse<QueryListPageResponse> r = HttpClientUtil.requestV2(request, new TypeReference<ShangLaiBaseResponse<QueryListPageResponse>>() {
                });
                paramMaps.clear();
                if (r == null || CollectionUtils.isEmpty(list) || CollectionUtils.isEmpty(r.getResult().getGoodsList())) {
                    break;
                }
                list.addAll(r.getResult().getGoodsList());
            }
            list.sort((o1, o2) -> o2.getGoods_price().compareTo(o1.getGoods_price()));
            maps.put(key, list);
        });

        return BaseResponse.executing();
    }

    @GetMapping("analyse/order")
    public BaseResponse<List<OrderDetailResponse>> analyseSuccess(@RequestParam String token
            , @RequestParam Integer timeInterval, @RequestParam Integer maxId, @RequestParam(required = false) String date) throws ParseException {

        TimeIntervalEnum timeIntervalType = Optional.ofNullable(TimeIntervalEnum.convert(timeInterval)).orElse(TimeIntervalEnum.MORNING);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date currentDate = simpleDateFormat.parse(date);
        Date date1 = Optional.ofNullable(currentDate).orElse(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        DateTime dateTime = new DateTime(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        Date startTime = timeIntervalType.getVipStartTime(dateTime.toLocalDate());
        Date endTime = new DateTime(startTime).plusSeconds(6000).toDate();

        String dateString = simpleDateFormat.format(date1);


        String key = dateString + "~~" + timeIntervalType.getCode();


        boolean fromCache = analyesMaps.containsKey(key);
        if (fromCache) {
            List<OrderDetailResponse> goodInfoResponseList = analyesMaps.get(key);
            return BaseResponse.success(goodInfoResponseList);
        }
        String template = "{\"id\":\"%s\",\"token\":\"%s\"}";

        List<OrderDetailResponse> list = new LinkedList<>();
        while (maxId > (maxId - 10000)) {
            String body = String.format(template, maxId, token);

            String url = requestHost + "/index/auction_goods/audit_payment_page";
            HttpRequestEntity requestEntity = HttpRequestEntity.builder()
                    .body(body)
                    .origin(requestHost)
                    .referer(requestHost + "/vue/")
                    .url(url)
                    .mediaType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                    .build();
            ShangLaiBaseResponse<OrderDetailResponse> s = HttpClientUtil.requestV2(requestEntity, new TypeReference<ShangLaiBaseResponse<OrderDetailResponse>>() {
            });
            if (s == null) {
                break;
            }
            maxId--;
            OrderDetailResponse orderDetailResponse = s.getInfo();
            if (orderDetailResponse == null || orderDetailResponse.getPay_time() == null) {
                continue;
            }
            if (orderDetailResponse.getCreate_time().after(endTime)) {
                continue;
            }
            if (orderDetailResponse.getCreate_time().after(startTime)) {
                list.add(orderDetailResponse);
            } else {
                break;
            }

        }
        if (!CollectionUtils.isEmpty(list)) {
            analyesMaps.put(key, list);
        }
        return BaseResponse.success(list);
    }

    @GetMapping("analyse/user")
    public BaseResponse<Map<String, Integer>> analyseUser(@RequestParam String token
            , @RequestParam Integer timeInterval, @RequestParam Integer maxId, @RequestParam(required = false) String date) throws ParseException {

        TimeIntervalEnum timeIntervalType = Optional.ofNullable(TimeIntervalEnum.convert(timeInterval)).orElse(TimeIntervalEnum.MORNING);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date currentDate = simpleDateFormat.parse(date);
        Date date1 = Optional.ofNullable(currentDate).orElse(new Date());

        String dateString = simpleDateFormat.format(date1);


        String key = dateString + "~~" + timeIntervalType.getCode();

        boolean fromCache = analyesMaps.containsKey(key);
        if (!fromCache) {
            analyseSuccess(token, timeInterval, maxId, date);
        }
        List<OrderDetailResponse> orderDetailResponses = analyesMaps.get(key);
        Map<String, List<OrderDetailResponse>> userOrders = orderDetailResponses.stream().collect(Collectors.groupingBy(OrderDetailResponse::getBuy_real_name));

        Map<String, Integer> userCount = new HashMap<>(userOrders.keySet().size());
        userOrders.forEach((key1, value) -> userCount.put(key1, value.size()));

        return BaseResponse.success(userCount);
    }


    private void splitStart(List<Integer> productIds, String token, TimeIntervalEnum timeInterval,
                            boolean isVip, String userName) {
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        ThreadPoolUtil.execute(() -> {
            try {
                Date startTime = isVip ? timeInterval.getVipStartTime(DateTime.now().toLocalDate()) : timeInterval.getStartTime();

                Date now = new Date();
                long startSecond = startTime.getTime();
                if (now.before(startTime)) {
                    long nowSecond = now.getTime();
                    log.info(simpleFormat.format(now) + ",已预约时间段：" + simpleFormat.format(startTime) + "," + (startSecond - nowSecond) + "毫秒后开始运行(" + userName + ")");
                    Thread.sleep(startSecond - nowSecond);
                }
            } catch (Exception ignored) {

            }
            log.info("抢购开始" + simpleFormat.format(new Date()));
            Thread.currentThread().setName("thread-商品-[" + StringUtils.join(productIds, ",") + "]");
            AtomicBoolean hasProduct = new AtomicBoolean(false);
            AtomicBoolean killFail = new AtomicBoolean(false);
            for (int i = 0; i < count; i++) {
                productIds.forEach(productId -> {
                    if (hasProduct.get()) {
                        log.info("已拍到产品，无需重拍");
                        return;
                    }
                    if (killFail.get()) {
                        log.info("已被其它用户拍走了");
                        return;
                    }
                    Map<String, String> paramMaps = new HashMap<>();
                    paramMaps.put("ag_id", String.valueOf(productId));
                    paramMaps.put("token", token);
                    paramMaps.put("visit", timeInterval.getVisit());
                    paramMaps.put("tid", String.valueOf(timeInterval.getTid()));

                    String body = StringHelper.parameterJoin(paramMaps);
                    try {
                        HttpRequestEntity requestEntity = HttpRequestEntity.builder()
                                .body(body)
                                .origin(requestHost)
                                .referer(requestHost + "/vue/")
                                .url(requestHost + "/index/fg/goods/goodsKillOrder")
                                .build();
                        String s = HttpClientUtil.requestV2(requestEntity, new TypeReference<String>() {
                        });
                        if (StringUtils.isNotBlank(s)) {
                            if (s.contains("成功")) {
                                hasProduct.set(true);
                                log.info("thread-商品-" + productId + "抢购成功了(" + userName + ")");

                            } else if (s.contains("该场次拍卖次数已用完")) {
                                hasProduct.set(true);
                                log.info("其它线程已经抢购成功，无需重拍(" + userName + ")");
                            } else if (s.contains("商品已其他用户被拍下")) {
                                killFail.set(true);
                                log.info(simpleFormat.format(new Date()) + "-" + productId + "被其它人抢走了");
                            } else {
                                log.info(simpleFormat.format(new Date()) + " " + s);
                            }
                        }
                    } catch (Exception ignored) {

                    }
                });
                if (i % 10 == 0) {
                    log.info(Thread.currentThread().getName() + "正在抢拍，不要着急," + i);
                }
                if (hasProduct.get()) {
                    break;
                }
                if (killFail.get()) {
                    break;
                }
            }
            log.info("抢购结束" + simpleFormat.format(new Date()));
        });

    }

    private void cleanLastData() {
        Date date = new Date();
        boolean update = DateTimeUtil.queryTimeStamp(date) - DateTimeUtil.queryTimeStamp(lastUpdateTime) > 86400;
        if (!update) {
            return;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String lastDateString = simpleDateFormat.format(lastUpdateTime);
        for (TimeIntervalEnum timeIntervalEnum : TimeIntervalEnum.values()) {
            String key = lastDateString + "~~" + timeIntervalEnum.getCode();
            maps.remove(key);
        }
        this.lastUpdateTime = new DateTime(date).plusSeconds(86400).toDate();
    }

}

