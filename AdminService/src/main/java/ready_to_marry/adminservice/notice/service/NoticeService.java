package ready_to_marry.adminservice.notice.service;

import ready_to_marry.adminservice.notice.dto.request.NoticeRequest;
import ready_to_marry.adminservice.notice.dto.response.NoticeDTO;
import ready_to_marry.adminservice.notice.dto.response.NoticeDetailResponse;
import ready_to_marry.adminservice.notice.dto.response.NoticeListResponse;

import java.util.List;

public interface NoticeService {

    List<NoticeDTO> getAll();
    NoticeDetailResponse getById(Long id);
    NoticeDetailResponse create(NoticeRequest request, Long adminId);
    NoticeDetailResponse update(Long id, NoticeRequest request, Long adminId);
    void delete(Long id, Long adminId);

    NoticeListResponse getAllPaged(int page, int size);
}
