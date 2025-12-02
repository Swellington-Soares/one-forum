package br.one.forum.exception.api;

import br.one.forum.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public final class AuthenticationTokenGenerationException extends ApiException {
    public AuthenticationTokenGenerationException() {
        super(
                "exception.authentication_token_generator",
                HttpStatus.INTERNAL_SERVER_ERROR,
                ExceptionType.ERR_TOKEN_GENERATOR.getValue()
        );
    }
}
