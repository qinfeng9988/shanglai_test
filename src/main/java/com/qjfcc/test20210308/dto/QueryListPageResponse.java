package com.qjfcc.test20210308.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.qjfcc.test20210308.dto.response.GoodInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryListPageResponse implements Serializable {

    @JsonProperty("GoodsList")
    private List<GoodInfoResponse> GoodsList;

    private PageController pageController;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageController implements Serializable {
        private Integer currentPage;
        private Boolean hasNext;
        private Integer nextPage;
        private Integer totalPages;
    }
}
