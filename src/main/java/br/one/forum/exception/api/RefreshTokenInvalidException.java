package br.one.forum.exception.api;

import br.one.forum.exception.ApiException;
import org.springframework.http.HttpStatus;

public class RefreshTokenInvalidException extends ApiException {
    public RefreshTokenInvalidException() {
        super("{exception.refresh-token-invalid}",
                HttpStatus.FORBIDDEN,
                null
        );
    }
}
