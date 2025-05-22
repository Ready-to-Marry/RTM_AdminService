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

    // 등록한 관리자 ID
    @Column(nullable = false, updatable = false)
    private Long adminId;

    // 등록일시 (Builder 사용 시 기본값 적용)
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private final LocalDateTime createdAt = LocalDateTime.now();
}
