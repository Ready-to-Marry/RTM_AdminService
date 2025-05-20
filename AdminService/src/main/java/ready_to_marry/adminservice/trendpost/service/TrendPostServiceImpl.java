package ready_to_marry.adminservice.trendpost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ready_to_marry.adminservice.common.exception.BusinessException;
import ready_to_marry.adminservice.common.exception.ErrorCode;
import ready_to_marry.adminservice.trendpost.dto.request.TrendPostRequest;
import ready_to_marry.adminservice.trendpost.dto.response.*;
import ready_to_marry.adminservice.trendpost.entity.TrendPost;
import ready_to_marry.adminservice.trendpost.repository.TrendPostRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrendPostServiceImpl implements TrendPostService {

    private final TrendPostRepository repository;


    // 1. Admin -> Trendpost 등록
    @Override
    @Transactional
    public TrendPostDetailResponse create(TrendPostRequest request, Long adminId) {
        TrendPost post = TrendPost.builder()
                .title(request.getTitle())
                .thumbnailUrl(request.getThumbnailUrl())
                .contentImageUrl(request.getContentImageUrl())
                .mainBanner(Boolean.TRUE.equals(request.getMainBanner()))
                .priority(request.getPriority())
                .adminId(adminId)
                .build();

        return TrendPostDetailResponse.from(repository.save(post));
    }

    // 2. Admin -> Trendpost 수정

    @Override
    @Transactional
    public TrendPostDetailResponse update(Long id, TrendPostRequest request, Long adminId) {
        TrendPost post = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!post.getAdminId().equals(adminId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        post.setTitle(request.getTitle());
        post.setThumbnailUrl(request.getThumbnailUrl());
        post.setContentImageUrl(request.getContentImageUrl());
        post.setMainBanner(Boolean.TRUE.equals(request.getMainBanner()));
        post.setPriority(request.getPriority());

        return TrendPostDetailResponse.from(post);
    }

    // 3. Admin -> Trendpost 삭제
    @Override
    @Transactional
    public void delete(Long id, Long adminId) {
        TrendPost post = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!post.getAdminId().equals(adminId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        repository.delete(post);
    }

    // 4. ALL -> 메인 배너용 포스트 리스트 조회
    @Override
    public List<TrendPostBannerResponse> getMainBanners() {
        return repository.findByMainBannerTrueOrderByPriorityAsc().stream()
                .map(TrendPostBannerResponse::from)
                .collect(Collectors.toList());
    }


    // 5. ALL -> 전체 트렌드 포스트 목록 조회
    @Override
    public TrendPostPagedResponse getAllPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TrendPost> paged = repository.findAll(pageable);

        List<TrendPostDTO> items = paged.stream()
                .map(TrendPostDTO::from)
                .toList();

        return new TrendPostPagedResponse(items, page, size, paged.getTotalElements());
    }


    // 6. ALL -> 상세 조회
    @Override
    public TrendPostDetailResponse getById(Long id) {
        TrendPost post = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        return TrendPostDetailResponse.from(post);
    }
}
