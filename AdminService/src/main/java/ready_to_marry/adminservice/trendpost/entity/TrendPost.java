package ready_to_marry.adminservice.trendpost.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class TrendPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trendPostId;

    private String thumbnailUrl;
    private String title;
    private String contentImageUrl;

    // 1. 메인 배너 노출 여부 -> true일 경우, priority 순서에 따라 정렬하여 노출
    @Column(nullable = false)
    private boolean mainBanner;

    // 2. mainBanner가 true일 때만 의미 있음 -> 낮은 숫자일수록 우선 노출
    private Integer priority;

    @Column(nullable = false, updatable = false)
    private Long adminId; // 등록한 관리자 ID

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 등록일시
}
