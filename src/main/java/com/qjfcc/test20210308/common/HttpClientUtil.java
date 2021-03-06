package com.qjfcc.test20210308.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.qjfcc.test20210308.dto.HttpRequestEntity;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
public class HttpClientUtil {
    final static OkHttpClient okHttpClient = new OkHttpClient();

    public static <T> T request(HttpRequestEntity requestEntity, ParameterizedTypeReference<T> parameterizedTypeReference) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        header.setOrigin(requestEntity.getOrigin());
//        header.setHost(new InetSocketAddress("pm.shanglai.art",80));
        header.add("Host", "pm.shanglai.art");
        header.add(HttpHeaders.COOKIE, "aliyungf_tc=8ae8aead8f23dd2758c91b45598b7355a9021b00bb050e35d704bf440fec5286;");
        header.add("Referer", requestEntity.getReferer());
        header.setAccept(Lists.newArrayList(MediaType.ALL));
        header.setContentLength(76);
        header.add("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Mobile Safari/537.36");
        HttpEntity<String> httpEntity = new HttpEntity<>(requestEntity.getBody(), header);


        ResponseEntity<T> responseEntity = restTemplate
                .exchange(requestEntity.getUrl(), HttpMethod.POST,
                        httpEntity,
                        parameterizedTypeReference);

        return responseEntity.getBody();


    }


    public static <T> T requestV2(HttpRequestEntity requestEntity, TypeReference<T> typeReference) {

        okhttp3.MediaType mediaType = okhttp3.MediaType.parse(requestEntity.getMediaType());
        RequestBody.create(requestEntity.getBody(), mediaType);
        Request request = new Request.Builder()
                .url(requestEntity.getUrl())
                .addHeader("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 14_4_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/8.0.5(0x18000527) NetType/WIFI Language/zh_CN")
                .addHeader("Content-type", requestEntity.getMediaType())
                .addHeader("Origin", requestEntity.getOrigin())
                .addHeader("Host", "ss.shanglai.art")
                .addHeader("Cookie", "aliyungf_tc=8ae8aead8f23dd2758c91b45598b7355a9021b00bb050e35d704bf440fec5286;")
                .addHeader("Referer", requestEntity.getReferer())
                .addHeader("Accept", MediaType.ALL_VALUE)
                .addHeader("Content-Length", "76")
                .post(RequestBody.create(requestEntity.getBody(), mediaType))
                .build();

        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            return JSON.parseObject(response.body().string(), typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}


