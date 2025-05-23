package ready_to_marry.adminservice.mainbanner.service;

import ready_to_marry.adminservice.mainbanner.dto.request.MainBannerRequest;
import ready_to_marry.adminservice.mainbanner.dto.response.MainBannerResponse;

import java.util.List;

public interface MainBannerService {
    void register(MainBannerRequest request);
    void update(Long id, MainBannerRequest request);
    void delete(Long id);
    List<MainBannerResponse> getAll();
}
