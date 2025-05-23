package ready_to_marry.adminservice.notice.dto.response;

import lombok.Builder;
import lombok.Getter;
import ready_to_marry.adminservice.notice.entity.Notice;

@Getter
@Builder
public class NoticeDTO {
    private Long noticeId;
    private String title;
    private String createdAt;

    public static NoticeDTO from(Notice n) {
        return NoticeDTO.builder()
                .noticeId(n.getNoticeId())
                .title(n.getTitle())
                .createdAt(n.getCreatedAt().toLocalDate().toString())
                .build();
    }
}
