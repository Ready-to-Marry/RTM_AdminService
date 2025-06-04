package ready_to_marry.adminservice.profile.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ready_to_marry.adminservice.profile.dto.request.AdminProfileRequest;
import ready_to_marry.adminservice.profile.entity.Admin;
import ready_to_marry.adminservice.profile.repository.AdminRepository;

@Service
@RequiredArgsConstructor
public class InternalAdminService {

    private final AdminRepository adminRepository;

    public Long getOrCreateAdminId(AdminProfileRequest request) {
        return adminRepository.findByNameAndDepartmentAndPhone(
                        request.getName(), request.getDepartment(), request.getPhone())
                .map(Admin::getAdminId)
                .orElseGet(() -> {
                    Admin newAdmin = Admin.builder()
                            .name(request.getName())
                            .department(request.getDepartment())
                            .phone(request.getPhone())
                            .build();
                    return adminRepository.save(newAdmin).getAdminId();
                });
    }
}