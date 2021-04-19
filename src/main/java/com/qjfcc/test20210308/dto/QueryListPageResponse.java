package com.qjfcc.test20210308.dto;

import com.qjfcc.test20210308.response.GoodInfoResponse;
import lombok.Data;

import java.util.List;

@Data
public class QueryListPageResponse {
    private List<GoodInfoResponse> GoodsList;

    private PageController pageController;

    @Data
    public static class PageController {
        private Integer currentPage;
        private Boolean hasNext;
        private Integer nextPage;
        private Integer totalPages;
    }
}
