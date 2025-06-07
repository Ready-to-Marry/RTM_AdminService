package ready_to_marry.adminservice.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponKafkaRequest {
    private Long couponId;
    private Long userId;
}
