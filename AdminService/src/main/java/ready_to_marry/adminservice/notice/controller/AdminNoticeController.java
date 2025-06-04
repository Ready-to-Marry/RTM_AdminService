package ready_to_marry.adminservice.notice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ready_to_marry.adminservice.common.dto.ApiResponse;
import ready_to_marry.adminservice.notice.dto.request.NoticeRequest;
import ready_to_marry.adminservice.notice.dto.response.NoticeDetailResponse;
import ready_to_marry.adminservice.notice.service.NoticeService;

@RestController
@RequestMapping("/notices/admin")
@RequiredArgsConstructor
public class AdminNoticeController {

    private final NoticeService service;

    // 1. Admin -> 공지사항 등록
    @PostMapping
    public ApiResponse<NoticeDetailResponse> create(@RequestBody NoticeRequest request,
                                                    @RequestHeader("X-Admin-Id") Long adminId) {
        return ApiResponse.success(service.create(request, adminId));
    }

    // 2. Admin -> 공지사항 수정
    @PatchMapping("/{id}")
    public ApiResponse<NoticeDetailResponse> update(@PathVariable Long id,
                                                    @RequestBody NoticeRequest request,
                                                    @RequestHeader("X-Admin-Id") Long adminId) {
        return ApiResponse.success(service.update(id, request, adminId));
    }

    // 3. Admin -> 공지사항 삭제
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestHeader("X-Admin-Id") Long adminId) {
        service.delete(id, adminId);
        return ApiResponse.success(null);
    }
}
