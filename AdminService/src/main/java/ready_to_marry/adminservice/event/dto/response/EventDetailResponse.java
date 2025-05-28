package ready_to_marry.adminservice.event.dto.response;

import lombok.Builder;
import lombok.Getter;
import ready_to_marry.adminservice.event.entity.Coupon;
import ready_to_marry.adminservice.event.entity.Event;

import java.time.LocalDateTime;

@Getter
@Builder
public class EventDetailResponse {
    // User -> 쿠폰 이벤트 클릭 -> 쿠폰 이벤트 상세 조회
    // 쿠폰 정보
    private Long couponId;
    private String title;
    private String discountType;
    private Long discountAmount;
    private int totalQuantity;
    private int issuedQuantity;
    private boolean isActive;
    private LocalDateTime issuedFrom;
    private LocalDateTime issuedUntil;
    private LocalDateTime availableFrom;
    private LocalDateTime availableUntil;

    // 이벤트 정보
    private Long eventId;
    private String eventTitle;
    private String eventThumbnailUrl;
    private LocalDateTime eventStartDate;
    private LocalDateTime eventEndDate;

    public static EventDetailResponse from(Coupon coupon, Event event) {
        return EventDetailResponse.builder()
                .couponId(coupon.getCouponId())
                .title(coupon.getTitle())
                .discountType(coupon.getDiscountType())
                .discountAmount(coupon.getDiscountAmount())
                .totalQuantity(coupon.getTotalQuantity())
                .issuedQuantity(coupon.getIssuedQuantity())
                .isActive(coupon.isActive())
                .issuedFrom(coupon.getIssuedFrom())
                .issuedUntil(coupon.getIssuedUntil())
                .availableFrom(coupon.getAvailableFrom())
                .availableUntil(coupon.getAvailableUntil())
                .eventId(event.getEventId())
                .eventTitle(event.getTitle())
                .eventThumbnailUrl(event.getThumbnailImageUrl())
                .eventStartDate(event.getStartDate())
                .eventEndDate(event.getEndDate())
                .build();
    }
}
