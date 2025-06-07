package ready_to_marry.adminservice.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ready_to_marry.adminservice.event.entity.CouponIssue;

public interface CouponIssueRepository extends JpaRepository<CouponIssue, Long> {
}
