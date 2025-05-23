package ready_to_marry.adminservice.event.service;

import org.springframework.web.multipart.MultipartFile;
import ready_to_marry.adminservice.event.dto.request.EventCreateRequest;
import ready_to_marry.adminservice.event.dto.request.EventUpdateRequest;
import ready_to_marry.adminservice.event.dto.response.*;

public interface EventService {

    // 1. 이벤트 등록
    void createEvent(EventCreateRequest request, MultipartFile image, Long adminId);

    // 2. 이벤트 수정
    void updateEvent(Long eventId, EventUpdateRequest request, MultipartFile image, Long adminId);

    // 3. 이벤트 삭제
    void deleteEvent(Long eventId, Long adminId);

    // 4. 이벤트 상세 조회 (linkType이 CE일 경우 coupon 정보 포함)
    EventDetailResponse getEventDetail(Long eventId);

    // 5. 전체 이벤트 목록 페이징 조회
    EventPagedResponse getPagedEvents(int page, int size);
}
