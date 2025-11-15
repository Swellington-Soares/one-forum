package br.one.forum.exception;

import org.springframework.http.HttpStatus;

public class TokenVerificationException  extends ApiException {
    public TokenVerificationException () {
        super(
                "exception.token-invalid",
                HttpStatus.BAD_REQUEST,
                ExceptionType.LOGIN_REQUEST.getValue()
        );
    }
}
