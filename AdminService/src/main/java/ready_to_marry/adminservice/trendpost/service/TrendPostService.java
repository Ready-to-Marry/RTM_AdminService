package ready_to_marry.adminservice.trendpost.service;

import org.springframework.web.multipart.MultipartFile;
import ready_to_marry.adminservice.trendpost.dto.request.TrendPostRequest;
import ready_to_marry.adminservice.trendpost.dto.response.*;

import java.util.List;

public interface TrendPostService {

    // 1. Admin -> 트렌드 포스트 등록 (이미지 포함)
    TrendPostDetailResponse create(TrendPostRequest request,
                                   MultipartFile thumbnail,
                                   MultipartFile contentImage,
                                   Long adminId);

    // 2. Admin -> 트렌드 포스트 수정 (이미지 포함, null 허용)
    TrendPostDetailResponse update(Long id,
                                   TrendPostRequest request,
                                   MultipartFile thumbnail,
                                   MultipartFile contentImage,
                                   Long adminId);

    // 3. Admin -> 트렌드 포스트 삭제
    void delete(Long id, Long adminId);

    // 4. Public -> 전체 트렌드 포스트 목록 (페이징)
    TrendPostPagedResponse getAllPaged(int page, int size);

    // 5. Public -> 단건 상세 조회
    TrendPostDetailResponse getById(Long id);
}
