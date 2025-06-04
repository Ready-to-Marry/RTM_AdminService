package ready_to_marry.adminservice.common.exception;

import lombok.Getter;

@Getter
public class InfrastructureException extends RuntimeException {

    private final int code;

    public InfrastructureException(int code, String message) {
        super(message);
        this.code = code;
    }

}
