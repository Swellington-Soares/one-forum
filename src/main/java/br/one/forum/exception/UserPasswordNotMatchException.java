package br.one.forum.exception;

import org.springframework.http.HttpStatus;

public class UserPasswordNotMatchException extends ApiException {
    public UserPasswordNotMatchException() {
        super(
                "exception.password-not-match",
                HttpStatus.BAD_REQUEST,
                ExceptionType.WRONG_CREDENTIAL.getValue()
        );
    }
}
