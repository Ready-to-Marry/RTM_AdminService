package ready_to_marry.adminservice.event.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ready_to_marry.adminservice.event.enums.LinkType;

import java.time.LocalDateTime;

@JsonFormat
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventCreateRequest {
    private LinkType linkType;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long targetId; // 상품 특가(SD)일 경우 itemId, 쿠폰(CE)일 경우 couponId
}
