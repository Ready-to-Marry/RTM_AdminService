package ready_to_marry.adminservice.mainbanner.service;

import ready_to_marry.adminservice.mainbanner.dto.request.MainBannerRequest;
import ready_to_marry.adminservice.mainbanner.dto.response.AdminMainBannerResponse;
import ready_to_marry.adminservice.mainbanner.dto.response.MainBannerResponse;

import java.util.List;

public interface MainBannerService {

    // 1. Admin -> 메인 배너 등록
    void register(MainBannerRequest request, Long adminId);

    // 2. Admin -> 메인 배너 수정
    void update(Long id, MainBannerRequest request, Long adminId);

    // 3. Admin -> 메인 배너 삭제
    void delete(Long id, Long adminId);

    // 4. Admin -> 메인 배너 상세 조회
    List<AdminMainBannerResponse> getAdminAll();

    // 5. User -> 메인 배너 전체 조회
    List<MainBannerResponse> getAll();
}
