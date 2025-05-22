package ready_to_marry.adminservice.event.service;

import org.springframework.web.multipart.MultipartFile;
import ready_to_marry.adminservice.event.dto.request.EventCreateRequest;
import ready_to_marry.adminservice.event.dto.request.EventUpdateRequest;
import ready_to_marry.adminservice.event.dto.response.*;

import java.util.List;

public interface EventService {

    // 1. 이벤트 등록
    void createEvent(EventCreateRequest request, MultipartFile image, Long adminId);

    // 2. 이벤트 수정
    void updateEvent(Long eventId, EventUpdateRequest request, MultipartFile image, Long adminId);

    // 3. 이벤트 삭제
    void deleteEvent(Long eventId, Long adminId);

    // 4. 상세 조회
    EventDetailResponse getEventDetail(Long eventId);

    // 5. 메인 배너용 조회
    List<EventBannerResponse> getMainBannerEvents();

    // 6. 전체 이벤트 목록 조회 (페이징)
    EventPagedResponse getPagedEvents(int page, int size);

    // 7. 쿠폰 발급 (Event에서 trigger)
    CouponDetailResponse issueCoupon(Long couponId, Long userId);
}
