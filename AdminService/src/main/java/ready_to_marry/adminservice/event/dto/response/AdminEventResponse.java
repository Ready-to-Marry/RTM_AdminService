package ready_to_marry.adminservice.event.dto.response;

import lombok.Builder;
import lombok.Data;
import ready_to_marry.adminservice.event.entity.Event;
import ready_to_marry.adminservice.event.enums.LinkType;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminEventResponse {

    private Long eventId;

    private LinkType linkType; // "SD" 또는 "CE"

    private String title;

    private String thumbnailImageUrl;

    private String linkUrl;

    private Long targetId;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Long adminId;

    private LocalDateTime createdAt;

    public static AdminEventResponse from(Event event) {
        return AdminEventResponse.builder()
                .eventId(event.getEventId())
                .linkType(event.getLinkType())
                .title(event.getTitle())
                .thumbnailImageUrl(event.getThumbnailImageUrl())
                .linkUrl(event.getLinkUrl())
                .targetId(event.getTargetId())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .adminId(event.getAdminId())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
