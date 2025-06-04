package ready_to_marry.adminservice.mainbanner.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ready_to_marry.adminservice.mainbanner.dto.request.MainBannerRequest;
import ready_to_marry.adminservice.mainbanner.dto.response.AdminMainBannerResponse;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class MainBannerServiceImpl implements MainBannerService {

    private final MainBannerRepository bannerRepository;
    private final EventRepository eventRepository;
    private final TrendPostRepository trendPostRepository;

    // 1. 메인 배너 등록 (Admin)
    @Transactional
    @Override
    public void register(MainBannerRequest request, Long adminId) {
        log.info("[메인배너 등록] 요청 - adminId={}, type={}, refId={}, priority={}",
                adminId, request.getType(), request.getRefId(), request.getPriority());

        MainBanner banner = MainBanner.builder()
                .type(request.getType())
                .refId(request.getRefId())
                .priority(request.getPriority())
                .createdBy(adminId)
                .build();
        bannerRepository.save(banner);

        log.info("[메인배너 등록 완료] bannerId={}, refId={}, type={}",
                banner.getMainBannerId(), banner.getRefId(), banner.getType());
    }

    // 2. 메인 배너 수정 (Admin)
    @Transactional
    @Override
    public void update(Long mainBannerId, MainBannerRequest request, Long adminId) {
        log.info("[메인배너 수정] 요청 - bannerId={}, adminId={}, newRefId={}, newType={}, newPriority={}",
                mainBannerId, adminId, request.getRefId(), request.getType(), request.getPriority());

        MainBanner banner = bannerRepository.findById(mainBannerId)
                .orElseThrow(() -> {
                    log.warn("[메인배너 수정 실패] 존재하지 않음 - bannerId={}", mainBannerId);
                    return new IllegalArgumentException("존재하지 않는 배너입니다.");
                });

        banner.update(request.getType(), request.getRefId(), request.getPriority(), adminId);

        log.info("[메인배너 수정 완료] bannerId={}, updatedRefId={}, updatedType={}",
                mainBannerId, request.getRefId(), request.getType());
    }

    // 3. 메인 배너 삭제 (Admin)
    @Transactional
    @Override
    public void delete(Long id, Long adminId) {
        log.info("[메인배너 삭제] 요청 - bannerId={}, adminId={}", id, adminId);

        MainBanner banner = bannerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[메인배너 삭제 실패] 존재하지 않음 - bannerId={}", id);
                    return new IllegalArgumentException("존재하지 않는 배너입니다.");
                });

        bannerRepository.delete(banner);

        log.info("[메인배너 삭제 완료] bannerId={}", id);
    }

    // 4. 메인 배너 전체 목록 조회 (Admin - 상세 정보 포함)
    @Override
    public List<AdminMainBannerResponse> getAdminAll() {
        log.debug("[메인배너 전체 조회 - 관리자] 요청");
        return bannerRepository.findAllByOrderByPriorityAsc().stream()
                .map(this::toAdminResponse)
                .collect(Collectors.toList());
    }

    // 5. 메인 배너 전체 목록 조회 (User - 썸네일, 링크만 제공)
    @Override
    public List<MainBannerResponse> getAll() {
        log.debug("[메인배너 전체 조회 - 사용자] 요청");
        return bannerRepository.findAllByOrderByPriorityAsc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // 5-1. User 응답용 DTO 변환
    private MainBannerResponse toResponse(MainBanner banner) {
        if (banner.getType() == BannerType.EVENT) {
            Event event = eventRepository.findById(banner.getRefId())
                    .orElseThrow(() -> {
                        log.error("[메인배너 변환 실패 - 사용자] 이벤트 정보 없음 - refId={}", banner.getRefId());
                        return new IllegalArgumentException("이벤트 정보 없음");
                    });

            return MainBannerResponse.builder()
                    .mainBannerId(banner.getMainBannerId())
                    .thumbnailImageUrl(event.getThumbnailImageUrl())
                    .linkUrl(event.getLinkUrl())
                    .build();
        } else {
            TrendPost post = trendPostRepository.findById(banner.getRefId())
                    .orElseThrow(() -> {
                        log.error("[메인배너 변환 실패 - 사용자] 트렌드포스트 정보 없음 - refId={}", banner.getRefId());
                        return new IllegalArgumentException("트렌드포스트 정보 없음");
                    });

            return MainBannerResponse.builder()
                    .mainBannerId(banner.getMainBannerId())
                    .thumbnailImageUrl(post.getThumbnailUrl())
                    .linkUrl("/trend-posts/" + post.getTrendPostId())
                    .build();
        }
    }

    // 4-1. Admin 응답용 DTO 변환
    private AdminMainBannerResponse toAdminResponse(MainBanner banner) {
        if (banner.getType() == BannerType.EVENT) {
            Event event = eventRepository.findById(banner.getRefId())
                    .orElseThrow(() -> {
                        log.error("[메인배너 변환 실패 - 관리자] 이벤트 정보 없음 - refId={}", banner.getRefId());
                        return new IllegalArgumentException("이벤트 정보 없음");
                    });

            return AdminMainBannerResponse.builder()
                    .mainBannerId(banner.getMainBannerId())
                    .type(banner.getType())
                    .refId(banner.getRefId())
                    .priority(banner.getPriority())
                    .thumbnailImageUrl(event.getThumbnailImageUrl())
                    .linkUrl(event.getLinkUrl())
                    .build();
        } else {
            TrendPost post = trendPostRepository.findById(banner.getRefId())
                    .orElseThrow(() -> {
                        log.error("[메인배너 변환 실패 - 관리자] 트렌드포스트 정보 없음 - refId={}", banner.getRefId());
                        return new IllegalArgumentException("트렌드포스트 정보 없음");
                    });

            return AdminMainBannerResponse.builder()
                    .mainBannerId(banner.getMainBannerId())
                    .type(banner.getType())
                    .refId(banner.getRefId())
                    .priority(banner.getPriority())
                    .thumbnailImageUrl(post.getThumbnailUrl())
                    .linkUrl("/trend-posts/" + post.getTrendPostId())
                    .build();
        }
    }
}
