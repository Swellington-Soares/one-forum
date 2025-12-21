package br.one.forum.exception.api;

import br.one.forum.exception.ApiException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {
    public UserNotFoundException() {
        super(
                "{exception.user-not-found}",
                HttpStatus.NOT_FOUND,
                null);
    }
}
