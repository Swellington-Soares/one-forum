package br.one.forum.exception;

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
