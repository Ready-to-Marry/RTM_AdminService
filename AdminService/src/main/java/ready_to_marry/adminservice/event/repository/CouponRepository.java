package ready_to_marry.adminservice.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ready_to_marry.adminservice.event.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
