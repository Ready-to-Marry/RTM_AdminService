package ready_to_marry.adminservice.common.exception;

import lombok.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 14xx: 비즈니스 로직 오류
    UNAUTHORIZED_ACCESS(1400, "You do not have permission to perform this action"),
    NOT_FOUND(1401, "Resource not found"),
    COUPON_INACTIVE(1402, "Coupon is inactive"),
    COUPON_EXPIRED(1403, "Coupon issuance period is invalid"),
    COUPON_SOLD_OUT(1404, "Coupon is sold out"),

    // 24xx: 시스템(인프라) 오류
    DB_READ_FAILURE(2400, "Failed to read data from the database"),
    DB_WRITE_FAILURE(2401, "Failed to write data to the database");

    private final int code;
    private final String message;
}
