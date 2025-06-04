package ready_to_marry.adminservice.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ready_to_marry.adminservice.common.dto.ApiResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. NotFoundException (예: 비즈니스 예외 - 404)
    @ExceptionHandler(NotFoundException.class)
    public ApiResponse<Void> handleNotFoundException(NotFoundException ex) {
        return ApiResponse.error(404, ex.getMessage());
    }

    // 2. BusinessException (예: 유효성, 요청 불가 등)
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }

    // 3. InfrastructureException (예: DB, 외부 API 오류 등)
    @ExceptionHandler(InfrastructureException.class)
    public ApiResponse<Void> handleInfraException(InfrastructureException ex) {
        return ApiResponse.error(ex.getCode(), "시스템 오류가 발생했습니다. 관리자에게 문의하세요.");
    }

    // 4. 예상 못 한 예외 (fallback)
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleAll(Exception ex) {
        return ApiResponse.error(500, "서버 오류가 발생했습니다.");
    }
}
