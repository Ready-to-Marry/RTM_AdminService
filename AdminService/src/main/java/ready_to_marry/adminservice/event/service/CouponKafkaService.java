package ready_to_marry.adminservice.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ready_to_marry.adminservice.common.exception.BusinessException;
import ready_to_marry.adminservice.common.exception.ErrorCode;
import ready_to_marry.adminservice.common.exception.NotFoundException;
import ready_to_marry.adminservice.event.dto.request.CouponKafkaRequest;
import ready_to_marry.adminservice.event.entity.Coupon;
import ready_to_marry.adminservice.event.entity.CouponIssue;
import ready_to_marry.adminservice.event.repository.CouponIssueRepository;
import ready_to_marry.adminservice.event.repository.CouponRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponKafkaService {

    private final CouponRedisService couponRedisService;
    private final CouponRepository couponRepository;
    private final CouponServiceImpl couponServiceImpl;


    @KafkaListener(topics = "coupon", groupId = "coupon-consumer")
    public void consumeCouponIssue(CouponKafkaRequest request) {
        Long couponId = request.getCouponId();
        Long userId = request.getUserId();
        System.out.println(couponId + userId);

        //TODO 추후 Redis 데이터 유무, DB 확인, 쿠폰 중복 발급 로직 정리해서 리팩 필수
        try {
            if (couponRedisService.isCouponAlreadyIssued(couponId, userId)) return;

            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new NotFoundException("쿠폰 없음"));

            if (!couponRedisService.decreaseCouponStock(couponId)) {
                couponRedisService.restoreStockIfNeededFromDb(coupon, couponId); // DB에서 복구
                if (!couponRedisService.decreaseCouponStock(couponId)) return; // 진짜 없음
            }

            LocalDateTime now = LocalDateTime.now();
            if (!coupon.isActive()) throw new BusinessException(ErrorCode.COUPON_INACTIVE);
            if (now.isBefore(coupon.getIssuedFrom()) || now.isAfter(coupon.getIssuedUntil()))
                throw new BusinessException(ErrorCode.COUPON_EXPIRED);

            coupon.setIssuedQuantity(coupon.getIssuedQuantity() + 1);

            CouponIssue couponIssue = CouponIssue.builder()
                    .userId(userId)
                    .coupon(coupon)
                    .build();

            couponServiceImpl.issueCoupon(couponIssue);
            couponRepository.save(coupon);
            System.out.println("db 저장 완료");

            couponRedisService.markCouponIssued(couponId, userId);
            log.info("쿠폰 발급 성공 - userId={}, couponId={}", userId, couponId);
        } catch (Exception e) {
            log.error("쿠폰 발급 처리 중 오류 - userId={}, couponId={}", userId, couponId, e);
            throw e;
        }
    }
}
