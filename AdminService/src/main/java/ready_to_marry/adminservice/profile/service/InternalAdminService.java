package ready_to_marry.adminservice.profile.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ready_to_marry.adminservice.profile.dto.request.AdminProfileRequest;
import ready_to_marry.adminservice.profile.entity.Admin;
import ready_to_marry.adminservice.profile.repository.AdminRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternalAdminService {

    private final AdminRepository adminRepository;

    public Long getOrCreateAdminId(AdminProfileRequest request) {
        log.info("[AdminProfile] 요청 정보 - name: {}, department: {}, phone: {}",
                request.getName(), request.getDepartment(), request.getPhone());

        return adminRepository.findByNameAndDepartmentAndPhone(
                        request.getName(), request.getDepartment(), request.getPhone())
                .map(admin -> {
                    log.info("[AdminProfile] 기존 관리자 조회됨 - adminId: {}", admin.getAdminId());
                    return admin.getAdminId();
                })
                .orElseGet(() -> {
                    try {
                        Admin newAdmin = Admin.builder()
                                .name(request.getName())
                                .department(request.getDepartment())
                                .phone(request.getPhone())
                                .build();
                        Admin saved = adminRepository.save(newAdmin);
                        log.info("[AdminProfile] 신규 관리자 생성됨 - adminId: {}", saved.getAdminId());
                        return saved.getAdminId();
                    } catch (Exception e) {
                        log.error("[AdminProfile] 관리자 생성 중 예외 발생", e);
                        throw e; // 그대로 전파 → GlobalExceptionHandler에서 처리됨
                    }
                });
    }
}
