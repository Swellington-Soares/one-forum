package br.one.forum.exception.api;

import br.one.forum.exception.ApiException;
import org.springframework.http.HttpStatus;

public class RefreshTokenExpiredException extends ApiException {
    public RefreshTokenExpiredException() {
        super(
                "{exception.refresh-token-expired}",
                HttpStatus.FORBIDDEN,
                null
        );
    }
}
