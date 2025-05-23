package ready_to_marry.adminservice.mainbanner.service;

import jakarta.transaction.Transactional;
import ready_to_marry.adminservice.mainbanner.dto.request.MainBannerRequest;
import ready_to_marry.adminservice.mainbanner.dto.response.MainBannerResponse;

import java.util.List;

public interface MainBannerService {
    void register(MainBannerRequest request, Long adminId);
    void update(Long id, MainBannerRequest request, Long adminId);
    void delete(Long id, Long adminId);

    @Transactional
    void register(MainBannerRequest request);

    @Transactional
    void update(Long mainBannerId, MainBannerRequest request);

    @Transactional
    void delete(Long id);

    List<MainBannerResponse> getAll();
}
