package br.one.forum.exception.api;

import br.one.forum.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public class UserPasswordNotMatchException extends ApiException {
    public UserPasswordNotMatchException() {
        super(
                "exception.password-not-match",
                HttpStatus.BAD_REQUEST,
                ExceptionType.BAD_CREDENTIALS.getValue()
        );
    }
}
