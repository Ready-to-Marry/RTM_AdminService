package ready_to_marry.adminservice.trendpost.service;

import ready_to_marry.adminservice.trendpost.dto.request.TrendPostRequest;
import ready_to_marry.adminservice.trendpost.dto.response.*;

import java.util.List;

public interface TrendPostService {

    // 1. Admin
    TrendPostDetailResponse create(TrendPostRequest request, Long adminId);
    TrendPostDetailResponse update(Long id, TrendPostRequest request, Long adminId);
    void delete(Long id, Long adminId);

    // 홈 배너용
    // 5. ALL -> 전체 트렌드 포스트 목록 조회
    TrendPostPagedResponse getAllPaged(int page, int size);

    TrendPostDetailResponse getById(Long id);           // 상세 조회
}
