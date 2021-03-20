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
        header.setAccept(Lists.newArrayList(MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.ALL));
        header.add("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Mobile Safari/537.36");
        HttpEntity<String> httpEntity = new HttpEntity<>(requestEntity.getBody(), header);

        ResponseEntity<T> responseEntity = restTemplate
                .exchange(requestEntity.getUrl(), HttpMethod.POST,
                        httpEntity,
                        parameterizedTypeReference);

        return responseEntity.getBody();

    }


}
