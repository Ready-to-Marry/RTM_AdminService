package ready_to_marry.adminservice.event.dto.response;

import lombok.Builder;
import lombok.Getter;
import ready_to_marry.adminservice.event.entity.Coupon;
import ready_to_marry.adminservice.event.entity.Event;
import ready_to_marry.adminservice.event.enums.LinkType;

import java.time.LocalDateTime;

@Getter
@Builder
public class EventDetailResponse {
    private Long eventId;
    private LinkType linkType;
    private String title;
    private String thumbnailImageUrl;
    private String linkUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private CouponResponse coupon; // 쿠폰 정보 (nullable)

    public static EventDetailResponse from(Event e, Coupon coupon) {
        return EventDetailResponse.builder()
                .eventId(e.getEventId())
                .linkType(e.getLinkType())
                .title(e.getTitle())
                .thumbnailImageUrl(e.getThumbnailImageUrl())
                .linkUrl(e.getLinkUrl())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .coupon(coupon != null ? CouponResponse.from(coupon) : null)
                .build();
    }
}
