package ready_to_marry.adminservice.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final S3Uploader s3Uploader;
    private final CouponService couponService;

    // 1. Admin -> 이벤트 등록
    @Override
    @Transactional
    public void createEvent(EventCreateRequest request, MultipartFile image, Long adminId) {
        log.info("[이벤트 등록] 요청 - adminId={}, title={}, linkType={}, targetId={}",
                adminId, request.getTitle(), request.getLinkType(), request.getTargetId());

        Long targetId = request.getTargetId();

        Event event = Event.builder()
                .linkType(request.getLinkType())
                .title(request.getTitle())
                .thumbnailImageUrl("")
                .linkUrl("")
                .targetId(targetId)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .adminId(adminId)
                .createdAt(LocalDateTime.now())
                .build();

        eventRepository.save(event);

        Long eventId = event.getEventId();
        String imageKey = String.format("admin/events/event-%d.jpg", eventId);
        String imageUrl = s3Uploader.uploadWithKey(image, imageKey);

        event.setThumbnailImageUrl(imageUrl);

        String linkUrl = (request.getLinkType() == LinkType.CE)
                ? "/events/" + eventId
                : "/catalog-service/items/" + targetId + "/details";

        event.setLinkUrl(linkUrl);

        log.info("[이벤트 등록 완료] eventId={}, linkUrl={}, imageUrl={}",
                eventId, linkUrl, imageUrl);
    }

    // 2. Admin -> 이벤트 수정
    @Override
    @Transactional
    public void updateEvent(Long eventId, EventUpdateRequest request, MultipartFile image, Long adminId) {
        log.info("[이벤트 수정] 요청 - eventId={}, adminId={}, newTitle={}",
                eventId, adminId, request.getTitle());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("[이벤트 수정 실패] 존재하지 않음 - eventId={}", eventId);
                    return new NotFoundException("이벤트를 찾을 수 없습니다.");
                });

        if (image != null && !image.isEmpty()) {
            String imageKey = String.format("admin/events/event-%d.jpg", eventId);
            String imageUrl = s3Uploader.uploadWithKey(image, imageKey);
            event.setThumbnailImageUrl(imageUrl);
            log.info("[이벤트 수정] 썸네일 이미지 업데이트 - eventId={}, imageUrl={}", eventId, imageUrl);
        }

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            event.setTitle(request.getTitle());
        }
        if (request.getStartDate() != null) {
            event.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            event.setEndDate(request.getEndDate());
        }

        log.info("[이벤트 수정 완료] eventId={}, updatedTitle={}", eventId, event.getTitle());
    }

    // 3. 이벤트 삭제
    @Override
    @Transactional
    public void deleteEvent(Long eventId, Long adminId) {
        log.info("[이벤트 삭제] 요청 - eventId={}, adminId={}", eventId, adminId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("[이벤트 삭제 실패] 존재하지 않음 - eventId={}", eventId);
                    return new NotFoundException("이벤트를 찾을 수 없습니다.");
                });

        eventRepository.delete(event);
        log.info("[이벤트 삭제 완료] eventId={}", eventId);

        // S3 이미지 삭제 로직이 필요한 경우 추가 가능
    }

    // 4. User -> 이벤트 상세 조회
    @Override
    public EventDetailResponse getEventDetail(Long eventId) {
        log.debug("[이벤트 상세 조회] 요청 - eventId={}", eventId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> {
                    log.warn("[이벤트 상세 조회 실패] 존재하지 않음 - eventId={}", eventId);
                    return new NotFoundException("이벤트를 찾을 수 없습니다.");
                });

        if (event.getLinkType() == LinkType.CE) {
            Coupon coupon = couponService.getCouponEntity(event.getTargetId());
            log.info("[이벤트 상세 조회 완료] 쿠폰 이벤트 - eventId={}, couponId={}",
                    eventId, coupon.getCouponId());
            return EventDetailResponse.from(coupon, event);
        }

        log.warn("[이벤트 상세 조회 실패] 쿠폰 이벤트 아님 - eventId={}, linkType={}",
                eventId, event.getLinkType());
        throw new IllegalStateException("쿠폰 이벤트가 아닌 이벤트는 상세 정보를 제공하지 않습니다.");
    }

    // 5. User -> 전체 이벤트 목록 조회
    @Override
    public EventPagedResponse getPagedEvents(int page, int size) {
        log.debug("[이벤트 목록 조회] 요청 - page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Event> result = eventRepository.findAll(pageable);

        List<EventDTO> items = result.getContent().stream()
                .map(EventDTO::from)
                .collect(Collectors.toList());

        log.info("[이벤트 목록 조회 완료] 총 {}건", result.getTotalElements());

        return EventPagedResponse.builder()
                .items(items)
                .page(result.getNumber())
                .size(result.getSize())
                .total((int) result.getTotalElements())
                .build();
    }

    // 6. Admin → 전체 이벤트 목록 조회
    @Override
    public List<AdminEventResponse> getAdminEventList() {
        log.debug("[이벤트 전체 조회 - 관리자] 요청");
        List<AdminEventResponse> list = eventRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(AdminEventResponse::from)
                .collect(Collectors.toList());
        log.info("[이벤트 전체 조회 완료 - 관리자] {}건 반환", list.size());
        return list;
    }
}

