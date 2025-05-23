package ready_to_marry.adminservice.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ready_to_marry.adminservice.common.exception.NotFoundException;
import ready_to_marry.adminservice.event.dto.request.CouponRequest;
import ready_to_marry.adminservice.event.dto.response.CouponDetailResponse;
import ready_to_marry.adminservice.event.entity.Coupon;
import ready_to_marry.adminservice.event.repository.CouponRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    // 1. 쿠폰 등록
    @Override
    @Transactional
    public CouponDetailResponse createCoupon(CouponRequest request, Long adminId) {
        Coupon coupon = couponRepository.save(Coupon.from(request, adminId));
        return CouponDetailResponse.from(coupon);
    }

    // 2. 쿠폰 수정 (adminId 반영)
    @Override
    @Transactional
    public void updateCoupon(Long couponId, CouponRequest request, Long adminId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new NotFoundException("해당 쿠폰을 찾을 수 없습니다."));
        coupon.updateFrom(request, adminId); // 수정자 기록
    }

    // 3. 쿠폰 삭제 (adminId 검증 or 기록 목적)
    @Override
    @Transactional
    public void deleteCoupon(Long couponId, Long adminId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new NotFoundException("해당 쿠폰을 찾을 수 없습니다."));

        // Optional: 삭제 권한 검증
        // if (!coupon.getCreatedBy().equals(adminId)) {
        //     throw new AccessDeniedException("삭제 권한이 없습니다.");
        // }

        couponRepository.delete(coupon);
    }

    // 4. 쿠폰 상세 조회
    @Override
    public CouponDetailResponse getCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new NotFoundException("해당 쿠폰을 찾을 수 없습니다."));
        return CouponDetailResponse.from(coupon);
    }

    // 5. 쿠폰 발급
    @Override
    @Transactional
    public CouponDetailResponse issueCoupon(Long couponId, Long userId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new NotFoundException("해당 쿠폰을 찾을 수 없습니다."));

        LocalDateTime now = LocalDateTime.now();
        if (!coupon.isActive()) throw new IllegalStateException("쿠폰이 비활성화 상태입니다.");
        if (now.isBefore(coupon.getIssuedFrom()) || now.isAfter(coupon.getIssuedUntil())) {
            throw new IllegalStateException("현재는 쿠폰 발급 기간이 아닙니다.");
        }
        if (coupon.getIssuedQuantity() >= coupon.getTotalQuantity()) {
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }

        coupon.setIssuedQuantity(coupon.getIssuedQuantity() + 1);
        return CouponDetailResponse.from(coupon);
    }

    // 6. 쿠폰 엔티티 반환 (내부 용도)
    @Override
    public Coupon getCouponEntity(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new NotFoundException("해당 쿠폰을 찾을 수 없습니다."));
    }

    // 7. 전체 쿠폰 목록 조회
    @Override
    public List<CouponDetailResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(CouponDetailResponse::from)
                .collect(Collectors.toList());
    }
}
