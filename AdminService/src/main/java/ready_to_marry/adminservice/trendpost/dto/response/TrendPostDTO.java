package ready_to_marry.adminservice.trendpost.dto.response;

import lombok.Builder;
import lombok.Getter;
import ready_to_marry.adminservice.trendpost.entity.TrendPost;

@Getter
@Builder
public class TrendPostDTO {
    private Long trendPostId;
    private String title;
    private String thumbnailUrl;

    public static TrendPostDTO from(TrendPost post) {
        return TrendPostDTO.builder()
                .trendPostId(post.getTrendPostId())
                .title(post.getTitle())
                .thumbnailUrl(post.getThumbnailUrl())
                .build();
    }
}