package com.qjfcc.test20210308.common;

import com.google.common.collect.Lists;
import com.qjfcc.test20210308.dto.HttpRequestEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class HttpClientUtil {

    public static <T> T request(HttpRequestEntity requestEntity,ParameterizedTypeReference<T> parameterizedTypeReference) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.setOrigin(requestEntity.getOrigin());
        header.add("Referer", requestEntity.getReferer());
        header.setAccept(Lists.newArrayList(MediaType.APPLICATION_FORM_URLENCODED, MediaType.ALL));
        header.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.128 Safari/537.36");
        header.add("Cookie","aliyungf_tc=1696d98052cc9d87ba797b983af2fc96be424ae4b38989d533090fe4a179cbfc; Hm_lvt_b59756204ba52a3f26f2dd24b8064e9d=1616897590,1618021263,1618623317,1618675409; Hm_lpvt_b59756204ba52a3f26f2dd24b8064e9d=1618680260");
        HttpEntity<String> httpEntity = new HttpEntity<>(requestEntity.getBody(), header);


        ResponseEntity<T> responseEntity = restTemplate
                .exchange(requestEntity.getUrl(), HttpMethod.POST,
                        httpEntity,
                        parameterizedTypeReference);

        return responseEntity.getBody();


    }


}
