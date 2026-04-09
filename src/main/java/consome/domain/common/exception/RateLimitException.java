package consome.domain.common.exception;

import lombok.Getter;

@Getter
public class RateLimitException extends RuntimeException {

    private final String code = "RATE_LIMIT_EXCEEDED";

    public RateLimitException() {
        super("요청이 너무 많습니다. 잠시 후 다시 시도해주세요.");
    }

    public RateLimitException(String message) {
        super(message);
    }
}
