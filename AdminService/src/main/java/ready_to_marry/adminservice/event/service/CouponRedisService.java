package ready_to_marry.adminservice.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ready_to_marry.adminservice.event.entity.Coupon;
import ready_to_marry.adminservice.event.repository.CouponRepository;

@Service
@RequiredArgsConstructor
public class CouponRedisService {
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 쿠폰 수량 초기화 (예: 관리자 등록 시)
     */
    public void setCouponStock(Long couponId, int quantity) {
        String stockKey = "coupon_stock:" + couponId;
        stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(quantity));
    }

    /**
     * 수량 하나 차감 (선착순 처리)
     */
    public boolean decreaseCouponStock(Long couponId) {
        String stockKey = "coupon_stock:" + couponId;
        Long remain = stringRedisTemplate.opsForValue().decrement(stockKey);
        return remain != null && remain >= 0;
    }

    /**
     * 유저가 이미 발급했는지 확인
     */
    public boolean isCouponAlreadyIssued(Long couponId, Long userId) {
        String issuedKey = "coupon_issued:" + couponId + ":" + userId;
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(issuedKey));
    }

    /**
     * 유저 발급 기록 저장
     */
    public void markCouponIssued(Long couponId, Long userId) {
        String issuedKey = "coupon_issued:" + couponId + ":" + userId;
        stringRedisTemplate.opsForValue().set(issuedKey, "1");
    }

    @Transactional(readOnly = true)
    public void restoreStockIfNeededFromDb(Coupon coupon, Long couponId) {
        String key = "coupon_stock:" + couponId;

        // Redis에 재고 키가 없거나 0 이하인지 확인
        String stockStr = stringRedisTemplate.opsForValue().get(key);
        if (stockStr != null && Integer.parseInt(stockStr) > 0) {
            return; // 재고 정상 → 복구 불필요
        }

        // 남은 재고 계산
        int remaining = coupon.getTotalQuantity() - coupon.getIssuedQuantity();
        if (remaining <= 0) {
            return; // 발급 가능한 재고 없음
        }

        // Redis에 재고 복구
        stringRedisTemplate.opsForValue().set(key, String.valueOf(remaining));
    }
}
