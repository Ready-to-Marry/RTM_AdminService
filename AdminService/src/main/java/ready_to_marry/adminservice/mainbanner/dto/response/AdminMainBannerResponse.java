package ready_to_marry.adminservice.mainbanner.dto.response;

import lombok.Builder;
import lombok.Data;
import ready_to_marry.adminservice.mainbanner.enums.BannerType;

@Data
@Builder
public class AdminMainBannerResponse {
    private Long mainBannerId;
    private BannerType type;
    private Long refId;
    private String thumbnailImageUrl;
    private String linkUrl;
    private int priority;
}
