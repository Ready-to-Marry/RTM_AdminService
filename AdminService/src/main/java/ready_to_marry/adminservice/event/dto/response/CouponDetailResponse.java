package ready_to_marry.adminservice.event.dto.response;

import lombok.Builder;
import lombok.Getter;
import ready_to_marry.adminservice.event.entity.Coupon;

import java.time.LocalDateTime;

@Getter
@Builder
public class CouponDetailResponse {
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

    public static CouponDetailResponse from(Coupon c) {
        return CouponDetailResponse.builder()
                .couponId(c.getCouponId())
                .title(c.getTitle())
                .discountType(c.getDiscountType())
                .discountAmount(c.getDiscountAmount())
                .totalQuantity(c.getTotalQuantity())
                .issuedQuantity(c.getIssuedQuantity())
                .isActive(c.isActive())
                .issuedFrom(c.getIssuedFrom())
                .issuedUntil(c.getIssuedUntil())
                .availableFrom(c.getAvailableFrom())
                .availableUntil(c.getAvailableUntil())
                .build();
    }
}