package com.qjfcc.test20210308.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.MediaType;

@Data
@Builder
public class HttpRequestEntity {
    private String origin;
    private String referer;
    private String body;
    private String url;
    private String mediaType;

    public String getMediaType() {
        if(mediaType == null){
            return MediaType.APPLICATION_FORM_URLENCODED_VALUE;
        }
        return mediaType;
    }
}
