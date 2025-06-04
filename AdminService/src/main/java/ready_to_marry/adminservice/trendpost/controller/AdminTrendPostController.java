package ready_to_marry.adminservice.trendpost.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ready_to_marry.adminservice.common.dto.ApiResponse;
import ready_to_marry.adminservice.trendpost.dto.request.TrendPostRequest;
import ready_to_marry.adminservice.trendpost.dto.response.TrendPostDetailResponse;
import ready_to_marry.adminservice.trendpost.service.TrendPostService;

@RestController
@RequestMapping("/trend-posts/admin")
@RequiredArgsConstructor
public class AdminTrendPostController {

    private final TrendPostService service;

    // 1. TrendPost 등록 (with 이미지 업로드)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<TrendPostDetailResponse> create(@RequestPart("request") TrendPostRequest request,
                                                       @RequestPart("thumbnail") MultipartFile thumbnail,
                                                       @RequestPart("contentImage") MultipartFile contentImage,
                                                       @RequestHeader("X-Admin-Id") Long adminId) {
        return ApiResponse.success(service.create(request, thumbnail, contentImage, adminId));
    }

    // 2. TrendPost 수정 (with 이미지 업로드)
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<TrendPostDetailResponse> update(@PathVariable Long id,
                                                       @RequestPart("request") TrendPostRequest request,
                                                       @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
                                                       @RequestPart(value = "contentImage", required = false) MultipartFile contentImage,
                                                       @RequestHeader("X-Admin-Id") Long adminId) {
        return ApiResponse.success(service.update(id, request, thumbnail, contentImage, adminId));
    }

    // 3. TrendPost 삭제
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestHeader("X-Admin-Id") Long adminId) {
        service.delete(id, adminId);
        return ApiResponse.success(null);
    }
}
