package br.one.forum.exception;

import org.springframework.http.HttpStatus;

public class ActionNotPermittedException extends ApiException {
    public ActionNotPermittedException() {
        super(
                "exception.action-not-permitted",
                HttpStatus.BAD_REQUEST,
                ExceptionType.NOT_PERMITTED.getValue()
        );

    }
}
