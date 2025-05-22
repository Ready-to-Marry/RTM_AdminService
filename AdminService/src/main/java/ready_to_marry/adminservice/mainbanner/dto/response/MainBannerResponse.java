package ready_to_marry.adminservice.mainbanner.dto.response;

import lombok.Builder;
import lombok.Getter;
import ready_to_marry.adminservice.mainbanner.enums.BannerType;

@Getter
@Builder
public class MainBannerResponse {
    private Long mainBannerId;
    private String thumbnailImageUrl;
    private String linkUrl;
}