package ready_to_marry.adminservice.trendpost.dto.response;

import lombok.Builder;
import lombok.Getter;
import ready_to_marry.adminservice.trendpost.entity.TrendPost;

@Getter
@Builder
public class TrendPostBannerResponse {
    private Long trendPostId;
    private String thumbnailUrl;

    public static TrendPostBannerResponse from(TrendPost post) {
        return TrendPostBannerResponse.builder()
                .trendPostId(post.getTrendPostId())
                .thumbnailUrl(post.getThumbnailUrl())
                .build();
    }
}
