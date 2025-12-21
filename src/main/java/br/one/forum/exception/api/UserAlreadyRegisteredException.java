package br.one.forum.exception.api;

import br.one.forum.exception.ApiException;
import org.springframework.http.HttpStatus;

public class UserAlreadyRegisteredException extends ApiException {
    public UserAlreadyRegisteredException() {
        super("{exception.user-already-registered}",
                HttpStatus.FORBIDDEN,
                null
                );
    }
}
