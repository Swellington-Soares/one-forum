package br.one.forum.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyRegisteredException extends ApiException {
    public UserAlreadyRegisteredException() {
        super("{exception.user-already-registered}",
                HttpStatus.FORBIDDEN,
                null
                );
    }
}
