package ready_to_marry.adminservice.notice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ready_to_marry.adminservice.common.exception.BusinessException;
import ready_to_marry.adminservice.common.exception.ErrorCode;
import ready_to_marry.adminservice.notice.dto.request.NoticeRequest;
import ready_to_marry.adminservice.notice.dto.response.NoticeDTO;
import ready_to_marry.adminservice.notice.dto.response.NoticeDetailResponse;
import ready_to_marry.adminservice.notice.dto.response.NoticeListResponse;
import ready_to_marry.adminservice.notice.entity.Notice;
import ready_to_marry.adminservice.notice.repository.NoticeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository repository;

    // 1. 공지사항 전체 목록 조회 (디버깅용)
    @Override
    public List<NoticeDTO> getAll() {
        log.debug("[공지사항 목록 조회] 요청 - 전체");
        return repository.findAll().stream()
                .map(NoticeDTO::from)
                .collect(Collectors.toList());
    }

    // 2. 페이징 목록 조회
    @Override
    public NoticeListResponse getAllPaged(int page, int size) {
        log.debug("[공지사항 목록 조회] 요청 - page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Notice> noticePage = repository.findAll(pageable);

        List<NoticeDTO> items = noticePage.stream()
                .map(NoticeDTO::from)
                .collect(Collectors.toList());

        return new NoticeListResponse(items, page, size, noticePage.getTotalElements());
    }

    // 3. 공지사항 상세 조회
    @Override
    public NoticeDetailResponse getById(Long id) {
        log.debug("[공지사항 단건 조회] 요청 - id={}", id);

        Notice notice = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[공지사항 조회 실패] 존재하지 않음 - id={}", id);
                    return new BusinessException(ErrorCode.NOT_FOUND);
                });

        return NoticeDetailResponse.from(notice);
    }

    // 4. 공지사항 등록
    @Override
    @Transactional
    public NoticeDetailResponse create(NoticeRequest request, Long adminId) {
        log.info("[공지사항 등록] 요청 - adminId={}, title={}", adminId, request.getTitle());

        Notice saved = repository.save(Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .adminId(adminId)
                .build());

        log.info("[공지사항 등록 완료] noticeId={}, title={}", saved.getNoticeId(), saved.getTitle());
        return NoticeDetailResponse.from(saved);
    }

    // 5. 공지사항 수정
    @Override
    @Transactional
    public NoticeDetailResponse update(Long id, NoticeRequest request, Long adminId) {
        log.info("[공지사항 수정] 요청 - id={}, adminId={}, newTitle={}", id, adminId, request.getTitle());

        Notice notice = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[공지사항 수정 실패] 존재하지 않음 - id={}", id);
                    return new BusinessException(ErrorCode.NOT_FOUND);
                });

        if (!notice.getAdminId().equals(adminId)) {
            log.warn("[공지사항 수정 실패] 권한 없음 - id={}, adminId={}", id, adminId);
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());

        log.info("[공지사항 수정 완료] id={}, newTitle={}", id, request.getTitle());
        return NoticeDetailResponse.from(notice);
    }

    // 6. 공지사항 삭제
    @Override
    @Transactional
    public void delete(Long id, Long adminId) {
        log.info("[공지사항 삭제] 요청 - id={}, adminId={}", id, adminId);

        Notice notice = repository.findById(id)
                .orElseThrow(() -> {
                    log.warn("[공지사항 삭제 실패] 존재하지 않음 - id={}", id);
                    return new BusinessException(ErrorCode.NOT_FOUND);
                });

        if (!notice.getAdminId().equals(adminId)) {
            log.warn("[공지사항 삭제 실패] 권한 없음 - id={}, adminId={}", id, adminId);
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        repository.delete(notice);
        log.info("[공지사항 삭제 완료] id={}", id);
    }
}
