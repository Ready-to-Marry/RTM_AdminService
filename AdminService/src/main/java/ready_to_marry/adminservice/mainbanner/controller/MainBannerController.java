package ready_to_marry.adminservice.mainbanner.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ready_to_marry.adminservice.mainbanner.dto.request.MainBannerRequest;
import ready_to_marry.adminservice.mainbanner.dto.response.MainBannerResponse;
import ready_to_marry.adminservice.mainbanner.service.MainBannerService;
import ready_to_marry.adminservice.common.dto.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/main-banners")
@RequiredArgsConstructor
public class MainBannerController {

    private final MainBannerService service;

    // 1. 메인 배너 등록
    @PostMapping("/admin")
    public ApiResponse<Void> register(@RequestBody MainBannerRequest request) {
        service.register(request);
        return ApiResponse.success(null);
    }

    // 2. 메인 배너 수정
    @PatchMapping("/admin/{mainBannerId}")
    public ApiResponse<Void> update(@PathVariable Long mainBannerId,
                                    @RequestBody MainBannerRequest request) {
        service.update(mainBannerId, request);
        return ApiResponse.success(null);
    }

    // 3. 메인 배너 삭제
    @DeleteMapping("/admin/{mainBannerId}")
    public ApiResponse<Void> delete(@PathVariable Long mainBannerId) {
        service.delete(mainBannerId);
        return ApiResponse.success(null);
    }

    // 4. 메인 배너 전체 조회
    @GetMapping
    public ApiResponse<List<MainBannerResponse>> getAll() {
        return ApiResponse.success(service.getAll());
    }
}
