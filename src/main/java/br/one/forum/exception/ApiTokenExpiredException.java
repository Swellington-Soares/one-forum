package br.one.forum.exception;

import org.springframework.http.HttpStatus;

public class ApiTokenExpiredException extends ApiException {
    public ApiTokenExpiredException() {
        super(
                "exception.token-expired",
                HttpStatus.FORBIDDEN,
                ExceptionType.ERR_TOKEN_GENERATOR.getValue()
        );
    }
}
