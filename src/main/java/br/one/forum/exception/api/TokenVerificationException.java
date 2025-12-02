package br.one.forum.exception.api;

import br.one.forum.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public class TokenVerificationException extends ApiException {
    public TokenVerificationException() {
        super(
                "exception.accessToken-invalid",
                HttpStatus.BAD_REQUEST,
                ExceptionType.LOGIN_REQUEST.getValue()
        );
    }
}
