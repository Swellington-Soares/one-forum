package br.one.forum.exception.api;

import br.one.forum.exception.ApiException;
import org.springframework.http.HttpStatus;

public class TokenVerificationException extends ApiException {
    public TokenVerificationException() {
        super(
                "{exception.token-verification-failed}",
                HttpStatus.BAD_REQUEST,
                null

        );
    }
}
