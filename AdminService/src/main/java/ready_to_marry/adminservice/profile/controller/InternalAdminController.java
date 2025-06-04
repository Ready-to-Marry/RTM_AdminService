package ready_to_marry.adminservice.profile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ready_to_marry.adminservice.profile.dto.request.AdminProfileRequest;
import ready_to_marry.adminservice.profile.dto.response.AdminIdResponse;
import ready_to_marry.adminservice.profile.service.InternalAdminService;
import ready_to_marry.adminservice.common.dto.ApiResponse;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalAdminController {

    private final InternalAdminService internalAdminService;

    // 내부 서비스 간 통신용 API
    @PostMapping("/admin-id")
    public ApiResponse<Long> getOrCreateAdminId(@RequestBody AdminProfileRequest request) {
        Long adminId = internalAdminService.getOrCreateAdminId(request);
        return ApiResponse.success(adminId);
    }

}
