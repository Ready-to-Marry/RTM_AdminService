package ready_to_marry.adminservice.event.dto.response;

import lombok.Builder;
import lombok.Getter;
import ready_to_marry.adminservice.event.entity.Event;

@Getter
@Builder
public class EventBannerResponse {
    private Long eventId;
    private String thumbnailImageUrl;
    private String linkUrl;

    public static EventBannerResponse from(Event e) {
        return EventBannerResponse.builder()
                .eventId(e.getEventId())
                .thumbnailImageUrl(e.getThumbnailImageUrl())
                .linkUrl(e.getLinkUrl())
                .build();
    }
}