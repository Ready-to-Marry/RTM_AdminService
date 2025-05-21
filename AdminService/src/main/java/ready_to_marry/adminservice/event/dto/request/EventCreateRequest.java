package ready_to_marry.adminservice.event.dto.request;

import lombok.Getter;
import ready_to_marry.adminservice.event.enums.LinkType;

import java.time.LocalDateTime;

@Getter
public class EventCreateRequest {
    private LinkType linkType;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean mainBanner;
    private Integer priority;

    private Long targetId;              // 상품 특가(SD) 이벤트
    private CouponRequest coupon;       // 쿠폰 이벤트(CE)
}
