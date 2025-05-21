package ready_to_marry.adminservice.event.dto.response;

import lombok.Builder;
import lombok.Getter;
import ready_to_marry.adminservice.event.entity.Coupon;

import java.time.LocalDateTime;

@Getter
@Builder
public class CouponResponse {
    private String title;
    private String discountType;
    private Long discountAmount;
    private LocalDateTime validUntil;
    private int totalQuantity;
    private int issuedQuantity;
    private LocalDateTime issuedFrom;
    private LocalDateTime issuedUntil;
    private LocalDateTime availableFrom;
    private LocalDateTime availableUntil;

    public static CouponResponse from(Coupon c) {
        return CouponResponse.builder()
                .title(c.getTitle())
                .discountType(c.getDiscountType())
                .discountAmount(c.getDiscountAmount())
                .validUntil(c.getValidUntil())
                .totalQuantity(c.getTotalQuantity())
                .issuedQuantity(c.getIssuedQuantity())
                .issuedFrom(c.getIssuedFrom())
                .issuedUntil(c.getIssuedUntil())
                .availableFrom(c.getAvailableFrom())
                .availableUntil(c.getAvailableUntil())
                .build();
    }
}
