package ready_to_marry.adminservice.trendpost.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ready_to_marry.adminservice.trendpost.entity.TrendPost;

import java.util.List;

public interface TrendPostRepository extends JpaRepository<TrendPost, Long> {

    // 1. 메인 배너에 노출할 포스트를 priority 기준으로 정렬하여 조회
    List<TrendPost> findByMainBannerTrueOrderByPriorityAsc();
}
