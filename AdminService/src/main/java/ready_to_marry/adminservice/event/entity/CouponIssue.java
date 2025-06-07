package ready_to_marry.adminservice.event.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "coupon_issue",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "coupon_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CouponIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 ID
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // 쿠폰 ID
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    // 발급 시간
    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    // 생성 시점 자동 입력
    @PrePersist
    public void prePersist() {
        this.issuedAt = LocalDateTime.now();
    }
}
