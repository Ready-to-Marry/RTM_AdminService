package ready_to_marry.adminservice.mainbanner.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    @Override
    @Transactional
    public void register(MainBannerRequest request) {
        MainBanner banner = MainBanner.builder()
                .type(request.getType())
                .refId(request.getRefId())
                .priority(request.getPriority())
                .build();
        bannerRepository.save(banner);
    }

    @Override
    @Transactional
    public void update(Long mainBannerId, MainBannerRequest request) {
        MainBanner banner = bannerRepository.findById(mainBannerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 배너입니다."));

        banner = MainBanner.builder()
                .mainBannerId(mainBannerId)
                .type(request.getType())
                .refId(request.getRefId())
                .priority(request.getPriority())
                .build();
        bannerRepository.save(banner);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        bannerRepository.deleteById(id);
    }

    @Override
    public List<MainBannerResponse> getAll() {
        return bannerRepository.findAllByOrderByPriorityAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private MainBannerResponse toResponse(MainBanner banner) {
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
    }
}
