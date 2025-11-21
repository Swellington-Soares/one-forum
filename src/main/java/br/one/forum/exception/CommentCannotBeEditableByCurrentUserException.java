package br.one.forum.exception;

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
