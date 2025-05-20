package ready_to_marry.adminservice.trendpost.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ready_to_marry.adminservice.common.dto.ApiResponse;
import ready_to_marry.adminservice.trendpost.dto.request.TrendPostRequest;
import ready_to_marry.adminservice.trendpost.dto.response.TrendPostDetailResponse;
import ready_to_marry.adminservice.trendpost.service.TrendPostService;

@RestController
@RequestMapping("/trend-posts/admin")
@RequiredArgsConstructor
public class AdminTrendPostController {

    private final TrendPostService service;

    @PostMapping
    public ApiResponse<TrendPostDetailResponse> create(@RequestBody TrendPostRequest request,
                                                       @RequestHeader("X-ADMIN-ID") Long adminId) {
        return ApiResponse.success(service.create(request, adminId));
    }

    @PatchMapping("/{id}")
    public ApiResponse<TrendPostDetailResponse> update(@PathVariable Long id,
                                                       @RequestBody TrendPostRequest request,
                                                       @RequestHeader("X-ADMIN-ID") Long adminId) {
        return ApiResponse.success(service.update(id, request, adminId));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @RequestHeader("X-ADMIN-ID") Long adminId) {
        service.delete(id, adminId);
        return ApiResponse.success(null);
    }
}
