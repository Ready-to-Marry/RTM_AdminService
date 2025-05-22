package ready_to_marry.adminservice.event.entity;

import jakarta.persistence.*;
import lombok.*;
import ready_to_marry.adminservice.event.enums.LinkType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LinkType linkType; // "SD" (상품 특가) or "CE" (쿠폰 이벤트)

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false)
    private String thumbnailImageUrl; // S3 업로드 결과

    @Column(nullable = false)
    private String linkUrl; // 사용자가 클릭 시 이동할 대상 링크

    @Column(nullable = false)
    private Long targetId; // ★ SD: itemId, CE: couponId

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private int priority;

    @Column(nullable = false)
    private boolean mainBanner;

    @Column(nullable = false, updatable = false)
    private Long adminId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
