package br.one.forum.exception;

import org.springframework.http.HttpStatus;

public final class AuthenticationTokenGenerationException extends ApiException {
    public AuthenticationTokenGenerationException() {
        super(
                "exception.authentication_token_generator",
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ERR_TOKEN_GENERATOR"
        );
    }
}
