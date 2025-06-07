package ready_to_marry.adminservice.event.dto.request;

import lombok.Getter;

@Getter
public class CouponKafkaRequest {
    private Long couponId;
    private Long userId;
}
