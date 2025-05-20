package ready_to_marry.adminservice.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NoticeListResponse {
    private List<NoticeDTO> items;
    private int page;
    private int size;
    private long total;
}