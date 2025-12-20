package br.one.forum.exception.api;

import br.one.forum.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public class UserAlreadyRegisteredException extends ApiException {
    public UserAlreadyRegisteredException() {
        super(
                "exception.user-already-exists",
                HttpStatus.BAD_REQUEST,
                ExceptionType.NOT_PERMITTED.getValue()
        );
    }
}
