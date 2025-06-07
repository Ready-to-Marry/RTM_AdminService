package ready_to_marry.adminservice.event.service;

import ready_to_marry.adminservice.event.dto.request.CouponRequest;
import ready_to_marry.adminservice.event.dto.response.CouponDetailResponse;
import ready_to_marry.adminservice.event.entity.Coupon;

import java.util.List;

public interface CouponService {

    // 1. 쿠폰 등록
    CouponDetailResponse createCoupon(CouponRequest request, Long adminId);

    // 2. 쿠폰 수정
    void updateCoupon(Long couponId, CouponRequest request, Long adminId);

    // 3. 쿠폰 삭제
    void deleteCoupon(Long couponId, Long adminId);

    // 4. 쿠폰 상세 조회
    CouponDetailResponse getCoupon(Long couponId);

    // 5. 쿠폰 전체 조회
    List<CouponDetailResponse> getAllCoupons();

    // 7. 내부 조회용: 쿠폰 엔티티 반환
    Coupon getCouponEntity(Long couponId);
}
