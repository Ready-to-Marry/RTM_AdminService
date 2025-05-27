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
public class EventUpdateRequest {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
