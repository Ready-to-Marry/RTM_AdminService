package ready_to_marry.adminservice.event.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponRequest {
    private String title;
    private String discountType;   // e.g., FIXED or PERCENT
    private Long discountAmount;

    private LocalDateTime validUntil;
    private int totalQuantity;

    private LocalDateTime issuedFrom;
    private LocalDateTime issuedUntil;
    private LocalDateTime availableFrom;
    private LocalDateTime availableUntil;
}