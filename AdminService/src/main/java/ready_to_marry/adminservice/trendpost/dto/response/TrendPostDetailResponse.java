package ready_to_marry.adminservice.trendpost.dto.response;

import lombok.Builder;
import lombok.Getter;
import ready_to_marry.adminservice.trendpost.entity.TrendPost;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrendPostDetailResponse {
    private Long trendPostId;
    private String title;
    private String thumbnailUrl;
    private String contentImageUrl;
    private LocalDateTime createdAt;

    public static TrendPostDetailResponse from(TrendPost post) {
        return TrendPostDetailResponse.builder()
                .trendPostId(post.getTrendPostId())
                .title(post.getTitle())
                .thumbnailUrl(post.getThumbnailUrl())
                .contentImageUrl(post.getContentImageUrl())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
