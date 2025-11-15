package br.one.forum.exception;

import org.springframework.http.HttpStatus;

public final class TopicNotFoundException extends ApiException {
    public TopicNotFoundException(int id) {
        super(
                "exception.topic-not-found",
                HttpStatus.NOT_FOUND,
                ExceptionType.RESOURCE_NOT_FOUND.getValue(),
                id
        );
    }
}
