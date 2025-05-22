package ready_to_marry.adminservice.event.service;

import ready_to_marry.adminservice.event.dto.request.CouponRequest;
import ready_to_marry.adminservice.event.dto.response.CouponDetailResponse;
import ready_to_marry.adminservice.event.entity.Coupon;

import java.util.List;

public interface CouponService {

    CouponDetailResponse createCoupon(CouponRequest request);

    void updateCoupon(Long couponId, CouponRequest request);

    void deleteCoupon(Long couponId);

    CouponDetailResponse getCoupon(Long couponId);

    List<CouponDetailResponse> getAllCoupons();

    CouponDetailResponse issueCoupon(Long couponId, Long userId);

    // 내부용 (엔티티 직접 반환)
    Coupon getCouponEntity(Long couponId);
}
