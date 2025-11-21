package br.one.forum.exception;

import org.springframework.http.HttpStatus;

public class TokenVerificationException  extends ApiException {
    public TokenVerificationException () {
        super(
                "exception.accessToken-invalid",
                HttpStatus.BAD_REQUEST,
                ExceptionType.LOGIN_REQUEST.getValue()
        );
    }
}
