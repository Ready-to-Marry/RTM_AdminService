package ready_to_marry.adminservice.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ready_to_marry.adminservice.common.dto.ApiResponse;
import ready_to_marry.adminservice.event.dto.request.CouponRequest;
import ready_to_marry.adminservice.event.dto.response.CouponDetailResponse;
import ready_to_marry.adminservice.event.service.CouponService;

import java.util.List;

@RestController
@RequestMapping("/coupons/admin")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;

    // 1. 쿠폰 등록
    @PostMapping
    public ApiResponse<CouponDetailResponse> createCoupon(@RequestBody CouponRequest request,
                                                          @RequestHeader("X-ADMIN-ID") Long adminId) {
        return ApiResponse.success(couponService.createCoupon(request, adminId));
    }

    // 2. 쿠폰 수정
    @PatchMapping("/{couponId}")
    public ApiResponse<Void> updateCoupon(@PathVariable Long couponId,
                                          @RequestBody CouponRequest request,
                                          @RequestHeader("X-ADMIN-ID") Long adminId) {
        couponService.updateCoupon(couponId, request, adminId);
        return ApiResponse.success(null);
    }

    // 3. 쿠폰 삭제
    @DeleteMapping("/{couponId}")
    public ApiResponse<Void> deleteCoupon(@PathVariable Long couponId,
                                          @RequestHeader("X-ADMIN-ID") Long adminId) {
        couponService.deleteCoupon(couponId, adminId);
        return ApiResponse.success(null);
    }

    // 4. 쿠폰 목록 조회
    @GetMapping
    public ApiResponse<List<CouponDetailResponse>> getAllCoupons() {
        return ApiResponse.success(couponService.getAllCoupons());
    }

    // 5. 쿠폰 상세 조회
    @GetMapping("/{couponId}")
    public ApiResponse<CouponDetailResponse> getCoupon(@PathVariable Long couponId) {
        return ApiResponse.success(couponService.getCoupon(couponId));
    }

}

