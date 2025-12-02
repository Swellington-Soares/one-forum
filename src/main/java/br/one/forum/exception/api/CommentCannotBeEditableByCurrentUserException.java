package br.one.forum.exception.api;

import br.one.forum.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public class CommentCannotBeEditableByCurrentUserException extends ApiException {
    public CommentCannotBeEditableByCurrentUserException() {
        super(
                "exception.comment-owner-incorrect",
                HttpStatus.FORBIDDEN,
                ExceptionType.BAD_CREDENTIALS.getValue()
        );
    }
}
