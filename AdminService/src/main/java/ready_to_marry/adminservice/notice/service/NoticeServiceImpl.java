package ready_to_marry.adminservice.notice.service;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository repository;

    // 1. 공지사항 전체 목록 조회 (페이징 없음, 기존 메서드)
    @Override
    public List<NoticeDTO> getAll() {
        return repository.findAll().stream()
                .map(NoticeDTO::from)
                .collect(Collectors.toList());
    }


    // 2. 페이징 처리된 공지사항 목록 조회 추가
    @Override
    public NoticeListResponse getAllPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Notice> noticePage = repository.findAll(pageable);

        List<NoticeDTO> items = noticePage.stream()
                .map(NoticeDTO::from)
                .collect(Collectors.toList());

        return new NoticeListResponse(
                items,
                page,
                size,
                noticePage.getTotalElements()
        );
    }

    // 3. 공지사항 상세 조회
    @Override
    public NoticeDetailResponse getById(Long id) {
        Notice notice = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));
        return NoticeDetailResponse.from(notice);
    }

    // 4. 공지사항 등록
    @Override
    @Transactional
    public NoticeDetailResponse create(NoticeRequest request, Long adminId) {
        Notice saved = repository.save(Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .adminId(adminId)
                .build());
        return NoticeDetailResponse.from(saved);
    }

    // 5. 공지사항 수정
    @Transactional
    @Override
    public NoticeDetailResponse update(Long id, NoticeRequest request, Long adminId) {
        Notice notice = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!notice.getAdminId().equals(adminId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        notice.setTitle(request.getTitle());
        notice.setContent(request.getContent());

        return NoticeDetailResponse.from(notice);
    }

    // 6. 공지사항 삭제
    @Transactional
    @Override
    public void delete(Long id, Long adminId) {
        Notice notice = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

        if (!notice.getAdminId().equals(adminId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        repository.delete(notice);
    }
}
