package br.one.forum.exception;

import org.springframework.http.HttpStatus;

public class TokenVerificationException  extends ApiException {
    public TokenVerificationException () {
        super(
                "exception.token-invalid",
                HttpStatus.BAD_REQUEST,
                ExceptionType.RESOURCE_NOT_FOUND.getValue()
        );
    }

    public TokenVerificationException(String message) {
        super(
                "exception.token-invalid",
                HttpStatus.UNAUTHORIZED,
                ExceptionType.RESOURCE_NOT_FOUND.getValue(),
                message
        );
    }
}
