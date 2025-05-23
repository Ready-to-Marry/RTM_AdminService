package ready_to_marry.adminservice.trendpost.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ready_to_marry.adminservice.common.dto.ApiResponse;
import ready_to_marry.adminservice.common.dto.Meta;
import ready_to_marry.adminservice.trendpost.dto.response.TrendPostBannerResponse;
import ready_to_marry.adminservice.trendpost.dto.response.TrendPostDetailResponse;
import ready_to_marry.adminservice.trendpost.dto.response.TrendPostDTO;
import ready_to_marry.adminservice.trendpost.dto.response.TrendPostPagedResponse;
import ready_to_marry.adminservice.trendpost.service.TrendPostService;

import java.util.List;

@RestController
@RequestMapping("/trend-posts")
@RequiredArgsConstructor
public class PublicTrendPostController {

    private final TrendPostService service;

    //1. 전체 TrendPost 목록 조회 -> @return List of TrendPostListResponse
    @GetMapping
    public ApiResponse<List<TrendPostDTO>> getPagedList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        TrendPostPagedResponse response = service.getAllPaged(page, size);

        Meta meta = Meta.builder()
                .page(response.getPage())
                .size(response.getSize())
                .total(response.getTotal())
                .totalPages((int) Math.ceil((double) response.getTotal() / response.getSize()))
                .build();

        return ApiResponse.success(response.getItems(), meta);
    }

    // 2. TrendPost 상세 조회 -> @param id 트렌드 포스트 ID / @return TrendPostDetailResponse
    @GetMapping("/{id}")
    public ApiResponse<TrendPostDetailResponse> detail(@PathVariable Long id) {
        return ApiResponse.success(service.getById(id));
    }
}