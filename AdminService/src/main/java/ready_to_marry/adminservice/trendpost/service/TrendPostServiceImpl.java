package ready_to_marry.adminservice.trendpost.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ready_to_marry.adminservice.common.exception.BusinessException;
import ready_to_marry.adminservice.common.exception.ErrorCode;
import ready_to_marry.adminservice.trendpost.dto.request.TrendPostRequest;
import ready_to_marry.adminservice.trendpost.dto.response.*;
import ready_to_marry.adminservice.trendpost.entity.TrendPost;
import ready_to_marry.adminservice.trendpost.repository.TrendPostRepository;
import ready_to_marry.adminservice.common.util.S3Uploader;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrendPostServiceImpl implements TrendPostService {

    private final TrendPostRepository repository;
    private final S3Uploader s3Uploader;

    @Override
    @Transactional
    public TrendPostDetailResponse create(TrendPostRequest request, MultipartFile thumbnail, MultipartFile contentImage, Long adminId) {
        log.info("[트렌드포스트 생성] 요청 - adminId={}, title={}", adminId, request.getTitle());

        TrendPost post = TrendPost.builder()
                .title(request.getTitle())
                .adminId(adminId)
                .build();
        repository.save(post);

        Long postId = post.getTrendPostId();

        String thumbnailKey = String.format("admin/trendposts/thumbnails/post-%d.jpg", postId);
        String contentKey = String.format("admin/trendposts/content/post-%d-content.jpg", postId);

        String thumbnailUrl = s3Uploader.uploadWithKey(thumbnail, thumbnailKey);
        String contentImageUrl = s3Uploader.uploadWithKey(contentImage, contentKey);

        post.setThumbnailUrl(thumbnailUrl);
        post.setContentImageUrl(contentImageUrl);

        log.info("[트렌드포스트 생성 완료] postId={}, thumbnailUrl={}, contentImageUrl={}", postId, thumbnailUrl, contentImageUrl);
        return TrendPostDetailResponse.from(post);
    }

    @Override
    @Transactional
    public TrendPostDetailResponse update(Long id, TrendPostRequest request, MultipartFile thumbnail, MultipartFile contentImage, Long adminId) {
        log.info("[트렌드포스트 수정] 요청 - postId={}, adminId={}, newTitle={}", id, adminId, request.getTitle());

        TrendPost post = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[트렌드포스트 수정 실패] 존재하지 않음 - postId={}", id);
                    return new BusinessException(ErrorCode.NOT_FOUND);
                });

        if (!post.getAdminId().equals(adminId)) {
            log.warn("[트렌드포스트 수정 실패] 권한 없음 - postId={}, adminId={}", id, adminId);
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        post.setTitle(request.getTitle());

        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailKey = String.format("admin/trendposts/thumbnails/post-%d.jpg", id);
            String thumbnailUrl = s3Uploader.uploadWithKey(thumbnail, thumbnailKey);
            post.setThumbnailUrl(thumbnailUrl);
            log.info("[트렌드포스트 수정] 썸네일 업데이트 - postId={}, thumbnailUrl={}", id, thumbnailUrl);
        }

        if (contentImage != null && !contentImage.isEmpty()) {
            String contentKey = String.format("admin/trendposts/content/post-%d-content.jpg", id);
            String contentImageUrl = s3Uploader.uploadWithKey(contentImage, contentKey);
            post.setContentImageUrl(contentImageUrl);
            log.info("[트렌드포스트 수정] 본문 이미지 업데이트 - postId={}, contentImageUrl={}", id, contentImageUrl);
        }

        return TrendPostDetailResponse.from(post);
    }

    @Override
    @Transactional
    public void delete(Long id, Long adminId) {
        log.info("[트렌드포스트 삭제] 요청 - postId={}, adminId={}", id, adminId);

        TrendPost post = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[트렌드포스트 삭제 실패] 존재하지 않음 - postId={}", id);
                    return new BusinessException(ErrorCode.NOT_FOUND);
                });

        if (!post.getAdminId().equals(adminId)) {
            log.warn("[트렌드포스트 삭제 실패] 권한 없음 - postId={}, adminId={}", id, adminId);
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        repository.delete(post);
        log.info("[트렌드포스트 삭제 완료] postId={}", id);
    }

    @Override
    public TrendPostPagedResponse getAllPaged(int page, int size) {
        log.debug("[트렌드포스트 목록 조회] 요청 - page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<TrendPost> paged = repository.findAll(pageable);

        List<TrendPostDTO> items = paged.stream()
                .map(TrendPostDTO::from)
                .toList();

        return new TrendPostPagedResponse(items, page, size, paged.getTotalElements());
    }

    @Override
    public TrendPostDetailResponse getById(Long id) {
        log.debug("[트렌드포스트 단건 조회] 요청 - postId={}", id);
        TrendPost post = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[트렌드포스트 조회 실패] 존재하지 않음 - postId={}", id);
                    return new BusinessException(ErrorCode.NOT_FOUND);
                });

        return TrendPostDetailResponse.from(post);
    }
}
