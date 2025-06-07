package ready_to_marry.adminservice.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ready_to_marry.adminservice.common.exception.*;
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
    private final CouponRedisService couponRedisService;

    // 1. 쿠폰 등록
    @Override
    @Transactional
    public CouponDetailResponse createCoupon(CouponRequest request, Long adminId) {
        try {
            Coupon coupon = couponRepository.save(Coupon.from(request, adminId));
            //TODO redis 저장 실패 트랜잭션 고려해서 재설계
            couponRedisService.setCouponStock(coupon.getCouponId(), coupon.getTotalQuantity());
            return CouponDetailResponse.from(coupon);
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_WRITE_FAILURE.getCode(), ErrorCode.DB_WRITE_FAILURE.getMessage());
        }
    }

    // 2. 쿠폰 수정
    @Override
    @Transactional
    public void updateCoupon(Long couponId, CouponRequest request, Long adminId) {
        try {
            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new NotFoundException("해당 쿠폰을 찾을 수 없습니다."));
            coupon.updateFrom(request, adminId);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_WRITE_FAILURE.getCode(), ErrorCode.DB_WRITE_FAILURE.getMessage());
        }
    }

    // 3. 쿠폰 삭제
    @Override
    @Transactional
    public void deleteCoupon(Long couponId, Long adminId) {
        try {
            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new NotFoundException("해당 쿠폰을 찾을 수 없습니다."));
            couponRepository.delete(coupon);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_WRITE_FAILURE.getCode(), ErrorCode.DB_WRITE_FAILURE.getMessage());
        }
    }

    // 4. 쿠폰 상세 조회
    @Override
    public CouponDetailResponse getCoupon(Long couponId) {
        try {
            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new NotFoundException("해당 쿠폰을 찾을 수 없습니다."));
            return CouponDetailResponse.from(coupon);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_READ_FAILURE.getCode(), ErrorCode.DB_READ_FAILURE.getMessage());
        }
    }

    // 6. 쿠폰 엔티티 반환 (내부 용도)
    @Override
    public Coupon getCouponEntity(Long couponId) {
        try {
            return couponRepository.findById(couponId)
                    .orElseThrow(() -> new NotFoundException("해당 쿠폰을 찾을 수 없습니다."));
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_READ_FAILURE.getCode(), ErrorCode.DB_READ_FAILURE.getMessage());
        }
    }

    // 7. 전체 쿠폰 목록 조회
    @Override
    public List<CouponDetailResponse> getAllCoupons() {
        try {
            return couponRepository.findAll().stream()
                    .map(CouponDetailResponse::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_READ_FAILURE.getCode(), ErrorCode.DB_READ_FAILURE.getMessage());
        }
    }
}
