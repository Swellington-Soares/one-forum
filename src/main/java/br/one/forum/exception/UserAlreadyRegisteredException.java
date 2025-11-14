package br.one.forum.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyRegisteredException extends ApiException {
    public UserAlreadyRegisteredException() {
        super(
                "exception.topic-not-found",
                HttpStatus.BAD_REQUEST,
                ExceptionType.RESOURCE_NOT_FOUND.getValue()
        );
    }
}
