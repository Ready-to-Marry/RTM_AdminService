package ready_to_marry.adminservice.trendpost.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrendPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trendPostId;

    private String thumbnailUrl;
    private String title;
    private String contentImageUrl;

    // 1. 메인 배너 노출 여부
    @Column(nullable = false)
    private boolean mainBanner;

    // 2. 우선순위 (mainBanner=true일 경우만 의미 있음)
    private Integer priority;

    // 등록한 관리자 ID
    @Column(nullable = false, updatable = false)
    private Long adminId;

    // 등록일시 (Builder 사용 시 기본값 적용)
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();
}
