package ready_to_marry.adminservice.event.entity;

import jakarta.persistence.*;
import lombok.*;

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
    private LocalDateTime validUntil;

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
}
