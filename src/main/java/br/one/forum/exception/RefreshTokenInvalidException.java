package br.one.forum.exception;

import org.springframework.http.HttpStatus;

public class RefreshTokenInvalidException extends ApiException {
    public RefreshTokenInvalidException() {
        super(
                "exception.refresh-token-invalid",
                HttpStatus.FORBIDDEN,
                ExceptionType.ERR_TOKEN_GENERATOR.getValue()
        );
    }
}
