package ready_to_marry.adminservice.common.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import ready_to_marry.adminservice.common.dto.ApiResponse;
import ready_to_marry.adminservice.common.dto.ErrorDetail;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 비즈니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex) {
        HttpStatus status = (ex.getErrorCode() == ErrorCode.UNAUTHORIZED_ACCESS)
                ? HttpStatus.FORBIDDEN
                : HttpStatus.OK;

        ApiResponse<Void> body = ApiResponse.error(ex.getCode(), ex.getMessage());
        return ResponseEntity.status(status).body(body);
    }

    // 2. 검증 오류 처리
    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestPartException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleValidation(Exception ex) {
        List<ErrorDetail> errors = new ArrayList<>();

        if (ex instanceof MethodArgumentNotValidException manv) {
            for (FieldError fieldError : manv.getBindingResult().getFieldErrors()) {
                errors.add(ErrorDetail.builder()
                        .field(fieldError.getField())
                        .message(fieldError.getDefaultMessage())
                        .build());
            }
        } else if (ex instanceof ConstraintViolationException cve) {
            for (ConstraintViolation<?> v : cve.getConstraintViolations()) {
                errors.add(ErrorDetail.builder()
                        .field(v.getPropertyPath().toString())
                        .message(v.getMessage())
                        .build());
            }
        } else if (ex instanceof MethodArgumentTypeMismatchException mtm) {
            errors.add(ErrorDetail.builder()
                    .field(mtm.getName())
                    .message("Parameter must be of type " + mtm.getRequiredType().getSimpleName())
                    .build());
        } else if (ex instanceof MissingServletRequestPartException msrp) {
            errors.add(ErrorDetail.builder()
                    .field(msrp.getRequestPartName())
                    .message("Required request part '" + msrp.getRequestPartName() + "' is not present")
                    .build());
        }

        ApiResponse<Void> body = ApiResponse.<Void>builder()
                .code(400)
                .message("Bad Request")
                .errors(errors)
                .build();

        return ResponseEntity.badRequest().body(body);
    }

    // 3. 권한 오류 처리
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(403, "접근이 거부되었습니다."));
    }

    // 4. 리소스 없음
    @ExceptionHandler({
            NotFoundException.class,
            NoSuchElementException.class,
            EntityNotFoundException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleNotFound(Exception ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(404, ex.getMessage()));
    }

    // 5. 시스템 오류 (인프라 오류 등)
    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ApiResponse<Void>> handleInfra(InfrastructureException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ex.getCode(), ex.getMessage()));
    }

    // 6. 그 외 예상치 못한 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknown(Exception ex) {
        log.error("⚠️ Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "서버 내부 오류가 발생했습니다."));
    }
}
