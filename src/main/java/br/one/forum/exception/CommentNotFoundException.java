package br.one.forum.exception;

import org.springframework.http.HttpStatus;

public final class CommentNotFoundException extends ApiException {

    public CommentNotFoundException(int id) {
        super(
                "exception.comment-not-found",
                HttpStatus.NOT_FOUND,
                ExceptionType.RESOURCE_NOT_FOUND.getValue(),
                id
        );
    }
}

