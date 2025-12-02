package br.one.forum.exception.api;

import br.one.forum.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public class AuthenticationCredentialException extends ApiException {

    public AuthenticationCredentialException() {
        super(
                "exception.incorrect-credentials",
                HttpStatus.BAD_REQUEST,
                ExceptionType.LOGIN_REQUEST.getValue()
        );
    }
}
