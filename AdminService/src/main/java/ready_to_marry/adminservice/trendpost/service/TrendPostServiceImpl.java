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

    // 1. Admin -> 등록
    @Override
    @Transactional
    public TrendPostDetailResponse create(TrendPostRequest request, MultipartFile thumbnail, MultipartFile contentImage, Long adminId) {
        try {
            String thumbnailUrl = s3Uploader.upload(thumbnail, "trendposts/thumbnails");
            String contentImageUrl = s3Uploader.upload(contentImage, "trendposts/content");

            TrendPost post = TrendPost.builder()
                    .title(request.getTitle())
                    .thumbnailUrl(thumbnailUrl)
                    .contentImageUrl(contentImageUrl)
                    .adminId(adminId)
                    .build();

            return TrendPostDetailResponse.from(repository.save(post));
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_WRITE_FAILURE.getCode(), ErrorCode.DB_WRITE_FAILURE.getMessage());
        }
    }

    // 2. Admin -> 수정 (이미지 선택적 변경)
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
    }

    // 3. Admin -> 삭제
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
    }

    // 4. 전체 목록
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
