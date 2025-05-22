package ready_to_marry.adminservice.mainbanner.dto.request;

import lombok.Getter;
import ready_to_marry.adminservice.mainbanner.enums.BannerType;

@Getter
public class MainBannerRequest {
    private BannerType type;
    private Long refId;
    private int priority;
}