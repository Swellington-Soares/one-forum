package br.one.forum.exception.api;

import br.one.forum.exception.ExceptionType;
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
