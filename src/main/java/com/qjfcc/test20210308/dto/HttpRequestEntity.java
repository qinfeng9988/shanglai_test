package com.qjfcc.test20210308.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HttpRequestEntity {
    private String origin;
    private String referer;
    private String body;
    private String url;
}
