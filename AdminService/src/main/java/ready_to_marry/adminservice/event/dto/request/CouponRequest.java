package ready_to_marry.adminservice.event.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponRequest {
    private String title;
    private String discountType;
    private Long discountAmount;

    private LocalDateTime validUntil;
    private Integer totalQuantity;

    private LocalDateTime issuedFrom;
    private LocalDateTime issuedUntil;

    private LocalDateTime availableFrom;
    private LocalDateTime availableUntil;
}