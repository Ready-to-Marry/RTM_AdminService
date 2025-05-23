package ready_to_marry.adminservice.mainbanner.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ready_to_marry.adminservice.common.exception.InfrastructureException;
import ready_to_marry.adminservice.common.exception.ErrorCode;
import ready_to_marry.adminservice.mainbanner.dto.request.MainBannerRequest;
import ready_to_marry.adminservice.mainbanner.dto.response.MainBannerResponse;
import ready_to_marry.adminservice.mainbanner.entity.MainBanner;
import ready_to_marry.adminservice.mainbanner.enums.BannerType;
import ready_to_marry.adminservice.mainbanner.repository.MainBannerRepository;
import ready_to_marry.adminservice.event.entity.Event;
import ready_to_marry.adminservice.event.repository.EventRepository;
import ready_to_marry.adminservice.trendpost.entity.TrendPost;
import ready_to_marry.adminservice.trendpost.repository.TrendPostRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MainBannerServiceImpl implements MainBannerService {

    private final MainBannerRepository bannerRepository;
    private final EventRepository eventRepository;
    private final TrendPostRepository trendPostRepository;

    @Transactional
    @Override
    public void register(MainBannerRequest request, Long adminId) {
        try {
            MainBanner banner = MainBanner.builder()
                    .type(request.getType())
                    .refId(request.getRefId())
                    .priority(request.getPriority())
                    .createdBy(adminId)
                    .build();
            bannerRepository.save(banner);
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_WRITE_FAILURE.getCode(), ErrorCode.DB_WRITE_FAILURE.getMessage());
        }
    }

    @Transactional
    @Override
    public void update(Long mainBannerId, MainBannerRequest request, Long adminId) {
        try {
            MainBanner banner = bannerRepository.findById(mainBannerId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배너입니다."));

            banner.update(request.getType(), request.getRefId(), request.getPriority(), adminId);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_WRITE_FAILURE.getCode(), ErrorCode.DB_WRITE_FAILURE.getMessage());
        }
    }

    @Transactional
    @Override
    public void delete(Long id, Long adminId) {
        try {
            MainBanner banner = bannerRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배너입니다."));
            bannerRepository.delete(banner);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_WRITE_FAILURE.getCode(), ErrorCode.DB_WRITE_FAILURE.getMessage());
        }
    }

    @Override
    public List<MainBannerResponse> getAll() {
        try {
            return bannerRepository.findAllByOrderByPriorityAsc().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_READ_FAILURE.getCode(), ErrorCode.DB_READ_FAILURE.getMessage());
        }
    }

    private MainBannerResponse toResponse(MainBanner banner) {
        try {
            if (banner.getType() == BannerType.EVENT) {
                Event event = eventRepository.findById(banner.getRefId())
                        .orElseThrow(() -> new IllegalArgumentException("이벤트 정보 없음"));

                return MainBannerResponse.builder()
                        .mainBannerId(banner.getMainBannerId())
                        .thumbnailImageUrl(event.getThumbnailImageUrl())
                        .linkUrl(event.getLinkUrl())
                        .build();
            } else {
                TrendPost post = trendPostRepository.findById(banner.getRefId())
                        .orElseThrow(() -> new IllegalArgumentException("트렌드포스트 정보 없음"));

                return MainBannerResponse.builder()
                        .mainBannerId(banner.getMainBannerId())
                        .thumbnailImageUrl(post.getThumbnailUrl())
                        .linkUrl("/trend-posts/" + post.getTrendPostId())
                        .build();
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(ErrorCode.DB_READ_FAILURE.getCode(), ErrorCode.DB_READ_FAILURE.getMessage());
        }
    }
}
