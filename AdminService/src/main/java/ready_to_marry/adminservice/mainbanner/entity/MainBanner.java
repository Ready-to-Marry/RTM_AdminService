package ready_to_marry.adminservice.mainbanner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ready_to_marry.adminservice.mainbanner.enums.BannerType;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MainBanner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mainBannerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BannerType type; // EVENT or TREND_POST

    @Column(nullable = false)
    private Long refId; // 실제 Event or TrendPost ID

    @Column(nullable = false)
    private int priority;

    @Column(nullable = false, updatable = false)
    private Long createdBy;

    @Column
    private Long modifiedBy;

    public void update(BannerType type, Long refId, int priority, Long adminId) {
        this.type = type;
        this.refId = refId;
        this.priority = priority;
        this.modifiedBy = adminId;
    }
}

