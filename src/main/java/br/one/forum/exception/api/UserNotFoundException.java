package br.one.forum.exception.api;

import br.one.forum.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ApiException {
    public UserNotFoundException(int id) {
        super(
                "exception.user-not-found",
                HttpStatus.NOT_FOUND,
                ExceptionType.RESOURCE_NOT_FOUND.getValue(),
                id
        );
    }

    public UserNotFoundException(String email) {
        super(
                "exception.user-not-found",
                HttpStatus.NOT_FOUND,
                ExceptionType.RESOURCE_NOT_FOUND.getValue(),
                email
        );
    }
}
