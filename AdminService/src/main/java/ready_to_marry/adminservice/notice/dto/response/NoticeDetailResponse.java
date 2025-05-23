package ready_to_marry.adminservice.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ready_to_marry.adminservice.notice.entity.Notice;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NoticeDetailResponse {
    private Long noticeId;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public static NoticeDetailResponse from(Notice n) {
        return new NoticeDetailResponse(
                n.getNoticeId(),
                n.getTitle(),
                n.getContent(),
                n.getCreatedAt()
        );
    }
}