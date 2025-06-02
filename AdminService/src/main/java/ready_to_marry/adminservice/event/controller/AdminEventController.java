package ready_to_marry.adminservice.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ready_to_marry.adminservice.common.dto.ApiResponse;
import ready_to_marry.adminservice.event.dto.request.EventCreateRequest;
import ready_to_marry.adminservice.event.dto.request.EventUpdateRequest;
import ready_to_marry.adminservice.event.dto.response.AdminEventResponse;
import ready_to_marry.adminservice.event.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/events/admin")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService service;

    // 1. Admin -> 이벤트 등록
    @PostMapping
    public ApiResponse<Void> createEvent(@RequestPart("eventRequest") EventCreateRequest request,
                                         @RequestPart("image") MultipartFile image,
                                         @RequestHeader("X-Admin-Id") Long adminId) {
        service.createEvent(request, image, adminId);
        return ApiResponse.success(null);
    }

    // 2. Admin -> 이벤트 수정
    @PatchMapping("/{id}")
    public ApiResponse<Void> updateEvent(@PathVariable Long id,
                                         @RequestPart("eventRequest") EventUpdateRequest request,
                                         @RequestPart(value = "image", required = false) MultipartFile image,
                                         @RequestHeader("X-Admin-Id") Long adminId) {
        service.updateEvent(id, request, image, adminId);
        return ApiResponse.success(null);
    }

    // 3. Admin -> 이벤트 삭제
    @DeleteMapping("/{eventId}")
    public ApiResponse<Void> deleteEvent(@PathVariable Long eventId,
                                         @RequestHeader("X-Admin-Id") Long adminId) {
        service.deleteEvent(eventId, adminId);
        return ApiResponse.success(null);
    }

    // 4. Admin -> 전체 이벤트 목록 조회
    @GetMapping
    public ApiResponse<List<AdminEventResponse>> getAllAdminEvents() {
        return ApiResponse.success(service.getAdminEventList());
    }
}
