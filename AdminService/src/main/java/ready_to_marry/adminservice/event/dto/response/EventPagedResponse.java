package ready_to_marry.adminservice.event.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EventPagedResponse {
    private int page;
    private int size;
    private long total;
    private List<EventDTO> items;
}
