package ready_to_marry.adminservice.event.entity;

import jakarta.persistence.*;
import lombok.*;
import ready_to_marry.adminservice.event.dto.request.CouponRequest;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String discountType; // FIXED or PERCENT

    @Column(nullable = false)
    private Long discountAmount;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int issuedQuantity;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private LocalDateTime issuedFrom; // 발급 시작일

    @Column(nullable = false)
    private LocalDateTime issuedUntil; // 발급 마감일

    @Column(nullable = false)
    private LocalDateTime availableFrom; // 사용 가능 시작일

    @Column(nullable = false)
    private LocalDateTime availableUntil; // 사용 가능 마감일

    @Column(nullable = false, updatable = false)
    private Long adminId;


    public static Coupon from(CouponRequest request, Long adminId) {
        return Coupon.builder()
                .title(request.getTitle())
                .discountType(request.getDiscountType())
                .discountAmount(request.getDiscountAmount())
                .totalQuantity(request.getTotalQuantity())
                .issuedQuantity(0)
                .isActive(true)
                .issuedFrom(request.getIssuedFrom())
                .issuedUntil(request.getIssuedUntil())
                .availableFrom(request.getAvailableFrom())
                .availableUntil(request.getAvailableUntil())
                .adminId(adminId)
                .build();
    }

    public void updateFrom(CouponRequest request) {
        this.title = request.getTitle();
        this.discountType = request.getDiscountType();
        this.discountAmount = request.getDiscountAmount();
        this.totalQuantity = request.getTotalQuantity();
        this.issuedFrom = request.getIssuedFrom();
        this.issuedUntil = request.getIssuedUntil();
        this.availableFrom = request.getAvailableFrom();
        this.availableUntil = request.getAvailableUntil();
    }
}
