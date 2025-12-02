package br.one.forum.exception.api;

import br.one.forum.exception.ExceptionType;
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
