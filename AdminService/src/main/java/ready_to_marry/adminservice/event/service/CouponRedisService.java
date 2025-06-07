package ready_to_marry.adminservice.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

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
}
