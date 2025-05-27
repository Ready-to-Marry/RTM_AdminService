package ready_to_marry.adminservice.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ready_to_marry.adminservice.common.exception.NotFoundException;
import ready_to_marry.adminservice.common.util.S3Uploader;
import ready_to_marry.adminservice.event.dto.request.EventCreateRequest;
import ready_to_marry.adminservice.event.dto.request.EventUpdateRequest;
import ready_to_marry.adminservice.event.dto.response.*;
import ready_to_marry.adminservice.event.entity.Event;
import ready_to_marry.adminservice.event.enums.LinkType;
import ready_to_marry.adminservice.event.repository.EventRepository;
import ready_to_marry.adminservice.event.entity.Coupon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final S3Uploader s3Uploader;
    private final CouponService couponService;

    // 1. 이벤트 등록
    @Override
    @Transactional
    public void createEvent(EventCreateRequest request, MultipartFile image, Long adminId) {
        Long targetId = request.getTargetId();

        // 1) 먼저 이벤트 저장 (이미지 제외)
        Event event = Event.builder()
                .linkType(request.getLinkType())
                .title(request.getTitle())
                .thumbnailImageUrl("") // 이미지 추후 설정
                .linkUrl("") // 추후 설정
                .targetId(targetId)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .adminId(adminId)
                .createdAt(LocalDateTime.now())
                .build();

        eventRepository.save(event); // ID 생성

        // 2) 이미지 업로드
        Long eventId = event.getEventId();
        String imageKey = String.format("admin/events/event-%d.jpg", eventId);
        String imageUrl = s3Uploader.uploadWithKey(image, imageKey);

        // 3) 이미지 URL, 링크 설정
        event.setThumbnailImageUrl(imageUrl);

        String linkUrl = (request.getLinkType() == LinkType.CE)
                ? "/events/" + eventId
                : "/catalog-service/items/" + targetId + "/details";

        event.setLinkUrl(linkUrl);
    }

    // 2. 이벤트 수정
    @Override
    @Transactional
    public void updateEvent(Long eventId, EventUpdateRequest request, MultipartFile image, Long adminId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));


        // 1) 이미지가 있으면 새로 업로드
        if (image != null && !image.isEmpty()) {
            String imageKey = String.format("admin/events/event-%d.jpg", eventId);
            String imageUrl = s3Uploader.uploadWithKey(image, imageKey);
            event.setThumbnailImageUrl(imageUrl);
        }

        // 2) 필드 갱신
        String title = request.getTitle();
        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate = request.getEndDate();

        if (title != null && !title.isEmpty()) {
            event.setTitle(title);
        }
        if (startDate != null) {
            event.setStartDate(startDate);
        }
        if (endDate != null) {
            event.setEndDate(endDate);
        }
    }

    // 3. 이벤트 삭제
    @Override
    @Transactional
    public void deleteEvent(Long eventId, Long adminId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));
        eventRepository.delete(event);

        // 이미지도 삭제할 경우
        // String imageKey = String.format("admin/events/event-%d.jpg", eventId);
        // s3Uploader.delete(imageKey);
    }

    // 4. 상세 조회
    @Override
    public EventDetailResponse getEventDetail(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

        if (event.getLinkType() == LinkType.CE) {
            Coupon coupon = couponService.getCouponEntity(event.getTargetId());
            return EventDetailResponse.from(coupon, event);
        }

        throw new IllegalStateException("쿠폰 이벤트가 아닌 이벤트는 상세 정보를 제공하지 않습니다.");
    }

    // 5. 전체 이벤트 목록 조회 (페이징)
    @Override
    public EventPagedResponse getPagedEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Event> result = eventRepository.findAll(pageable);

        List<EventDTO> items = result.getContent().stream()
                .map(EventDTO::from)
                .collect(Collectors.toList());

        return EventPagedResponse.builder()
                .items(items)
                .page(result.getNumber())
                .size(result.getSize())
                .total((int) result.getTotalElements())
                .build();
    }
}
