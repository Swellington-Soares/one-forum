package br.one.forum.exception.api;

import org.springframework.http.HttpStatus;

import static br.one.forum.exception.ExceptionType.ERR_TOKEN_GENERATOR;

public class TokenNotFoundException extends ApiException {
    public TokenNotFoundException() {
        super(
                "exception.token-not-found",
                HttpStatus.FORBIDDEN,
                ERR_TOKEN_GENERATOR.getValue()
        );
    }
}
