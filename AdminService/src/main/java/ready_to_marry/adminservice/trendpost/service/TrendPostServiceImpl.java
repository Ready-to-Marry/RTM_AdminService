package ready_to_marry.adminservice.trendpost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ready_to_marry.adminservice.common.exception.BusinessException;
import ready_to_marry.adminservice.common.exception.ErrorCode;
import ready_to_marry.adminservice.common.exception.InfrastructureException;
import ready_to_marry.adminservice.trendpost.dto.request.TrendPostRequest;
import ready_to_marry.adminservice.trendpost.dto.response.*;
import ready_to_marry.adminservice.trendpost.entity.TrendPost;
import ready_to_marry.adminservice.trendpost.repository.TrendPostRepository;
import ready_to_marry.adminservice.common.util.S3Uploader;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrendPostServiceImpl implements TrendPostService {

    private final TrendPostRepository repository;
    private final S3Uploader s3Uploader;

    // 1. Admin → 등록
    @Override
    @Transactional
    public TrendPostDetailResponse create(TrendPostRequest request, MultipartFile thumbnail, MultipartFile contentImage, Long adminId) {
        // 1) 먼저 저장 (이미지 제외)
        TrendPost post = TrendPost.builder()
                .title(request.getTitle())
                .adminId(adminId)
                .build();
        repository.save(post); // postId 생성

        Long postId = post.getTrendPostId();

        // 2) S3 업로드 (postId 포함한 고유 키)
        String thumbnailKey = String.format("admin/trendposts/thumbnails/post-%d.jpg", postId);
        String contentKey = String.format("admin/trendposts/content/post-%d-content.jpg", postId);

        String thumbnailUrl = s3Uploader.uploadWithKey(thumbnail, thumbnailKey);
        String contentImageUrl = s3Uploader.uploadWithKey(contentImage, contentKey);

        // 3) 이미지 URL 저장
        post.setThumbnailUrl(thumbnailUrl);
        post.setContentImageUrl(contentImageUrl);

        return TrendPostDetailResponse.from(post);
    }

    // 2. Admin → 수정 (이미지 선택적 변경)
    @Override
    @Transactional
    public TrendPostDetailResponse update(Long id, TrendPostRequest request, MultipartFile thumbnail, MultipartFile contentImage, Long adminId) {
        try {
            TrendPost post = repository.findById(id)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

            if (!post.getAdminId().equals(adminId)) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
            }

            post.setTitle(request.getTitle());

            if (thumbnail != null && !thumbnail.isEmpty()) {
                String thumbnailUrl = s3Uploader.upload(thumbnail, "trendposts/thumbnails");
                post.setThumbnailUrl(thumbnailUrl);
            }

            if (contentImage != null && !contentImage.isEmpty()) {
                String contentImageUrl = s3Uploader.upload(contentImage, "trendposts/content");
                post.setContentImageUrl(contentImageUrl);
            }

            return TrendPostDetailResponse.from(post);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_WRITE_FAILURE.getCode(), ErrorCode.DB_WRITE_FAILURE.getMessage());
        }

        post.setTitle(request.getTitle());

        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailKey = String.format("admin/trendposts/thumbnails/post-%d.jpg", id);
            String thumbnailUrl = s3Uploader.uploadWithKey(thumbnail, thumbnailKey);
            post.setThumbnailUrl(thumbnailUrl);
        }

        if (contentImage != null && !contentImage.isEmpty()) {
            String contentKey = String.format("admin/trendposts/content/post-%d-content.jpg", id);
            String contentImageUrl = s3Uploader.uploadWithKey(contentImage, contentKey);
            post.setContentImageUrl(contentImageUrl);
        }

        return TrendPostDetailResponse.from(post);
    }

    // 3. Admin → 삭제
    @Override
    @Transactional
    public void delete(Long id, Long adminId) {
        try {
            TrendPost post = repository.findById(id)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

            if (!post.getAdminId().equals(adminId)) {
                throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
            }

            repository.delete(post);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_WRITE_FAILURE.getCode(), ErrorCode.DB_WRITE_FAILURE.getMessage());
        }

//        //S3 이미지 삭제 로직 추가 가능
//        String thumbKey = String.format("admin/trendposts/thumbnails/post-%d.jpg", id);
//        String contentKey = String.format("admin/trendposts/content/post-%d-content.jpg", id);
//        // \\s3Uploader.delete(thumbKey);
//        // s3Uploader.delete(contentKey);

        repository.delete(post);
    }

    // 4. 전체 목록 조회
    @Override
    public TrendPostPagedResponse getAllPaged(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<TrendPost> paged = repository.findAll(pageable);

            List<TrendPostDTO> items = paged.stream()
                    .map(TrendPostDTO::from)
                    .toList();

            return new TrendPostPagedResponse(items, page, size, paged.getTotalElements());
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_READ_FAILURE.getCode(), ErrorCode.DB_READ_FAILURE.getMessage());
        }
    }

    // 5. 단건 조회
    @Override
    public TrendPostDetailResponse getById(Long id) {
        try {
            TrendPost post = repository.findById(id)
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

            return TrendPostDetailResponse.from(post);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_READ_FAILURE.getCode(), ErrorCode.DB_READ_FAILURE.getMessage());
        }
    }
}
