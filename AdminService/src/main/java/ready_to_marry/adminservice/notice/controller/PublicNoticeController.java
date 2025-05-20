package ready_to_marry.adminservice.notice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ready_to_marry.adminservice.common.dto.ApiResponse;
import ready_to_marry.adminservice.common.dto.Meta;
import ready_to_marry.adminservice.notice.dto.response.NoticeDTO;
import ready_to_marry.adminservice.notice.dto.response.NoticeDetailResponse;
import ready_to_marry.adminservice.notice.dto.response.NoticeListResponse;
import ready_to_marry.adminservice.notice.service.NoticeService;

import java.util.List;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class PublicNoticeController {

    private final NoticeService service;

    // 1. ALL : 공지사항 목록 조회 + 페이징
    @GetMapping
    public ApiResponse<List<NoticeDTO>> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        NoticeListResponse resp = service.getAllPaged(page, size);
        Meta meta = Meta.builder()
                .page(resp.getPage())
                .size(resp.getSize())
                .total(resp.getTotal())
                .totalPages((int) Math.ceil((double) resp.getTotal() / resp.getSize()))
                .build();

        return ApiResponse.success(resp.getItems(), meta);
    }

    // 2. ALL : 공지사항 상세 조회
    @GetMapping("/{id}")
    public ApiResponse<NoticeDetailResponse> getById(@PathVariable Long id) {
        return ApiResponse.success(service.getById(id));
    }
}