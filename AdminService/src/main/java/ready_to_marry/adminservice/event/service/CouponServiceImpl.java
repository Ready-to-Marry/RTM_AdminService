package ready_to_marry.adminservice.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ready_to_marry.adminservice.common.exception.NotFoundException;
import ready_to_marry.adminservice.event.dto.request.CouponRequest;
import ready_to_marry.adminservice.event.dto.response.CouponDetailResponse;
import ready_to_marry.adminservice.event.entity.Coupon;
import ready_to_marry.adminservice.event.repository.CouponRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    // 1. 쿠폰 등록
    @Override
    @Transactional
    public CouponDetailResponse createCoupon(CouponRequest request, Long adminId) {
        Coupon coupon = couponRepository.save(Coupon.from(request, adminId));
        log.info("[쿠폰 등록] 성공 - couponId={}, title={}, adminId={}",
                coupon.getCouponId(), coupon.getTitle(), adminId);
        return CouponDetailResponse.from(coupon);
    }

    // 2. 쿠폰 수정
    @Override
    @Transactional
    public void updateCoupon(Long couponId, CouponRequest request, Long adminId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> {
                    log.warn("[쿠폰 수정 실패] 존재하지 않는 쿠폰 - couponId={}", couponId);
                    return new NotFoundException("해당 쿠폰을 찾을 수 없습니다.");
                });

        coupon.updateFrom(request, adminId);
        log.info("[쿠폰 수정] 성공 - couponId={}, adminId={}, newTitle={}",
                couponId, adminId, request.getTitle());
    }

    // 3. 쿠폰 삭제
    @Override
    @Transactional
    public void deleteCoupon(Long couponId, Long adminId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> {
                    log.warn("[쿠폰 삭제 실패] 존재하지 않는 쿠폰 - couponId={}", couponId);
                    return new NotFoundException("해당 쿠폰을 찾을 수 없습니다.");
                });

        couponRepository.delete(coupon);
        log.info("[쿠폰 삭제] 성공 - couponId={}, adminId={}", couponId, adminId);
    }

    // 4. 쿠폰 상세 조회
    @Override
    public CouponDetailResponse getCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> {
                    log.warn("[쿠폰 상세 조회 실패] 존재하지 않는 쿠폰 - couponId={}", couponId);
                    return new NotFoundException("해당 쿠폰을 찾을 수 없습니다.");
                });

        log.debug("[쿠폰 상세 조회] 성공 - couponId={}", couponId);
        return CouponDetailResponse.from(coupon);
    }

    // 5. 쿠폰 발급
    @Override
    @Transactional
    public CouponDetailResponse issueCoupon(Long couponId, Long userId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> {
                    log.warn("[쿠폰 발급 실패] 존재하지 않는 쿠폰 - couponId={}, userId={}", couponId, userId);
                    return new NotFoundException("해당 쿠폰을 찾을 수 없습니다.");
                });

        LocalDateTime now = LocalDateTime.now();
        if (!coupon.isActive()) {
            log.warn("[쿠폰 발급 실패] 비활성 상태 - couponId={}, userId={}", couponId, userId);
            throw new IllegalStateException("쿠폰이 비활성화 상태입니다.");
        }
        if (now.isBefore(coupon.getIssuedFrom()) || now.isAfter(coupon.getIssuedUntil())) {
            log.warn("[쿠폰 발급 실패] 기간 외 요청 - couponId={}, userId={}, now={}, from={}, until={}",
                    couponId, userId, now, coupon.getIssuedFrom(), coupon.getIssuedUntil());
            throw new IllegalStateException("현재는 쿠폰 발급 기간이 아닙니다.");
        }
        if (coupon.getIssuedQuantity() >= coupon.getTotalQuantity()) {
            log.warn("[쿠폰 발급 실패] 소진 완료 - couponId={}, issued={}, total={}",
                    couponId, coupon.getIssuedQuantity(), coupon.getTotalQuantity());
            throw new IllegalStateException("쿠폰이 모두 소진되었습니다.");
        }

        coupon.setIssuedQuantity(coupon.getIssuedQuantity() + 1);
        log.info("[쿠폰 발급 성공] couponId={}, userId={}, issuedQuantity={}",
                couponId, userId, coupon.getIssuedQuantity());

        return CouponDetailResponse.from(coupon);
    }

    // 6. 쿠폰 엔티티 조회 (내부 용도)
    @Override
    public Coupon getCouponEntity(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> {
                    log.warn("[쿠폰 엔티티 조회 실패] 존재하지 않는 쿠폰 - couponId={}", couponId);
                    return new NotFoundException("해당 쿠폰을 찾을 수 없습니다.");
                });
    }

    // 7. 전체 쿠폰 목록 조회
    @Override
    public List<CouponDetailResponse> getAllCoupons() {
        List<Coupon> list = couponRepository.findAll();
        log.debug("[쿠폰 전체 목록 조회] {}건 반환", list.size());
        return list.stream()
                .map(CouponDetailResponse::from)
                .collect(Collectors.toList());
    }
}
