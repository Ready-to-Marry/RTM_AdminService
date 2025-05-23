package ready_to_marry.adminservice.event.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ready_to_marry.adminservice.common.exception.*;
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
        try {
            String imageUrl = s3Uploader.upload(image, "events");
            Long targetId = request.getTargetId();

            Event event = Event.builder()
                    .linkType(request.getLinkType())
                    .title(request.getTitle())
                    .thumbnailImageUrl(imageUrl)
                    .linkUrl("") // 추후 설정
                    .targetId(targetId)
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .adminId(adminId)
                    .createdAt(LocalDateTime.now())
                    .build();

            eventRepository.save(event);

            String linkUrl = (request.getLinkType() == LinkType.CE)
                    ? "/events/" + event.getEventId()
                    : "/catalog-service/items/" + targetId + "/details";

            event.setLinkUrl(linkUrl);
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_WRITE_FAILURE.getCode(), ErrorCode.DB_WRITE_FAILURE.getMessage());
        }
    }

    // 2. 이벤트 수정
    @Override
    @Transactional
    public void updateEvent(Long eventId, EventUpdateRequest request, MultipartFile image, Long adminId) {
        try {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

            if (!event.getLinkType().equals(request.getLinkType()) ||
                    !event.getTargetId().equals(request.getTargetId())) {
                throw new IllegalStateException("linkType 또는 targetId는 수정할 수 없습니다. 삭제 후 다시 등록해주세요.");
            }

            String imageUrl = (image != null) ? s3Uploader.upload(image, "events") : event.getThumbnailImageUrl();

            event.setTitle(request.getTitle());
            event.setStartDate(request.getStartDate());
            event.setEndDate(request.getEndDate());
            event.setThumbnailImageUrl(imageUrl);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_WRITE_FAILURE.getCode(), ErrorCode.DB_WRITE_FAILURE.getMessage());
        }
    }

    // 3. 이벤트 삭제
    @Override
    @Transactional
    public void deleteEvent(Long eventId, Long adminId) {
        try {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));
            eventRepository.delete(event);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_WRITE_FAILURE.getCode(), ErrorCode.DB_WRITE_FAILURE.getMessage());
        }
    }

    // 4. 상세 조회
    @Override
    public EventDetailResponse getEventDetail(Long eventId) {
        try {
            Event event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new NotFoundException("이벤트를 찾을 수 없습니다."));

            if (event.getLinkType() == LinkType.CE) {
                Coupon coupon = couponService.getCouponEntity(event.getTargetId());
                return EventDetailResponse.from(coupon, event);
            }

            // 상품 특가(SD)는 자체 상세 정보는 없고, 링크만 존재하므로 null 반환
            return null;
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_READ_FAILURE.getCode(), ErrorCode.DB_READ_FAILURE.getMessage());
        }
    }

    // 5. 전체 이벤트 목록 조회 (페이징)
    @Override
    public EventPagedResponse getPagedEvents(int page, int size) {
        try {
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
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_READ_FAILURE.getCode(), ErrorCode.DB_READ_FAILURE.getMessage());
        }
    }
}
