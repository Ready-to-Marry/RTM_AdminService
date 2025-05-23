package ready_to_marry.adminservice.event.dto.response;

import lombok.Builder;
import lombok.Getter;
import ready_to_marry.adminservice.event.entity.Event;

import java.time.LocalDateTime;

@Getter
@Builder
public class EventDTO {
    private Long eventId;
    private String title;
    private String thumbnailImageUrl;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String linkUrl;

    public static EventDTO from(Event e) {
        return EventDTO.builder()
                .eventId(e.getEventId())
                .title(e.getTitle())
                .thumbnailImageUrl(e.getThumbnailImageUrl())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .linkUrl(e.getLinkUrl())
                .build();
    }
}