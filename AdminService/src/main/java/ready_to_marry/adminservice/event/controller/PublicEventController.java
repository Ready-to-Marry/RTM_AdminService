package ready_to_marry.adminservice.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ready_to_marry.adminservice.common.dto.ApiResponse;
import ready_to_marry.adminservice.common.dto.Meta;
import ready_to_marry.adminservice.event.dto.response.EventBannerResponse;
import ready_to_marry.adminservice.event.dto.response.EventDTO;
import ready_to_marry.adminservice.event.dto.response.EventDetailResponse;
import ready_to_marry.adminservice.event.dto.response.EventPagedResponse;
import ready_to_marry.adminservice.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService service;

    // 1. 전체 이벤트 목록 조회 (사용자 페이지에서 이벤트 목록 출력용)
    // 페이징 처리 포함 / 썸네일 + 제목 + 기간 등 목록 요약 정보 제공
    @GetMapping
    public ApiResponse<List<EventDTO>> getEvents(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        EventPagedResponse response = service.getPagedEvents(page, size);
        Meta meta = Meta.of(response.getPage(), response.getSize(), response.getTotal());
        return ApiResponse.success(response.getItems(), meta);
    }

    // 2. 이벤트 상세 조회
    // - linkType이 'sd' -> item 상세 페이지로 연결되는 정보 포함
    // - linkType이 'ce' -> coupon 상세 정보 포함
    @GetMapping("/{eventId}")
    public ApiResponse<EventDetailResponse> getDetail(@PathVariable Long eventId) {
        return ApiResponse.success(service.getEventDetail(eventId));
    }
}