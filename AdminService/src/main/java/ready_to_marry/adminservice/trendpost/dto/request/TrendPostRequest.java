package ready_to_marry.adminservice.trendpost.dto.request;

import lombok.Getter;

@Getter
public class TrendPostRequest {
    private String thumbnailUrl;
    private String title;
    private String contentImageUrl;
    private Boolean mainBanner;   // true일 경우 priority 필요
    private Integer priority;
}
