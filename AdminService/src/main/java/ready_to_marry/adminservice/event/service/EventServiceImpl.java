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

    @Override
    @Transactional
    public void createEvent(EventCreateRequest request, MultipartFile image, Long adminId) {
        String imageUrl = s3Uploader.upload(image, "events");
        Long targetId = request.getTargetId();

        // 1. 이벤트 먼저 저장 (임시로 linkUrl 비워둠)
        Event event = Event.builder()
                .linkType(request.getLinkType())
                .title(request.getTitle())
                .thumbnailImageUrl(imageUrl)
                .linkUrl("") // 나중에 분기하여 설정
                .targetId(targetId)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .mainBanner(request.isMainBanner())
                .priority(request.getPriority())
                .adminId(adminId)
                .createdAt(LocalDateTime.now())
                .build();

        eventRepository.save(event); // save 이후 eventId 생성됨

        // 2. 이벤트 타입에 따라 linkUrl 설정
        String linkUrl = (request.getLinkType() == LinkType.CE)
                ? "/events/" + event.getEventId() // 쿠폰 이벤트 → 이벤트 상세 페이지
                : "/catalog-service/items/" + targetId + "/details"; // 상품 특가 → 바로 item 상세

        event.setLinkUrl(linkUrl);
    }


    @Override
    @Transactional
// ★ 이벤트 수정 폼에서 linkType, targetId는 수정 불가 필드
    public void updateEvent(Long eventId, EventUpdateRequest request, MultipartFile image, Long adminId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

        // linkType이나 targetId가 변경되었는지 확인
        if (!event.getLinkType().equals(request.getLinkType()) ||
                !event.getTargetId().equals(request.getTargetId())) {
            throw new IllegalStateException("linkType 또는 targetId는 수정할 수 없습니다. 이벤트를 삭제 후 다시 등록해주세요.");
        }

        // 이미지가 있다면 새로 업로드, 없다면 기존 이미지 유지
        String imageUrl = (image != null) ? s3Uploader.upload(image, "events") : event.getThumbnailImageUrl();

        // 나머지 필드 수정
        event.setTitle(request.getTitle());
        event.setStartDate(request.getStartDate());
        event.setEndDate(request.getEndDate());
        event.setMainBanner(request.isMainBanner());
        event.setPriority(request.getPriority());
        event.setThumbnailImageUrl(imageUrl);
    }

    @Override
    @Transactional
    public void deleteEvent(Long eventId, Long adminId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));
        eventRepository.delete(event);
    }

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

    @Override
    public List<EventBannerResponse> getMainBannerEvents() {
        return eventRepository.findByMainBannerTrueOrderByPriorityAsc().stream()
                .map(EventBannerResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CouponDetailResponse issueCoupon(Long couponId, Long userId) {
        return couponService.issueCoupon(couponId, userId);
    }

    @Override
    public EventPagedResponse getPagedEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Event> pageResult = eventRepository.findAll(pageable);

        List<EventDTO> items = pageResult.getContent().stream()
                .map(EventDTO::from)
                .collect(Collectors.toList());

        return EventPagedResponse.builder()
                .items(items)
                .page(pageResult.getNumber())
                .size(pageResult.getSize())
                .total((int) pageResult.getTotalElements())
                .build();
    }
}
