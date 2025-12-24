package br.one.forum.exception.api;

import br.one.forum.exception.ApiException;
import org.springframework.http.HttpStatus;

public class TokenNotFoundException extends ApiException {

    public TokenNotFoundException() {
        super(
                "{exception.token-not-found}",
                HttpStatus.FORBIDDEN,
                null
        );
    }
}
