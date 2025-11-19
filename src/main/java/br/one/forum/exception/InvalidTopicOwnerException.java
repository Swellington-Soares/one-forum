package br.one.forum.exception;

import org.springframework.http.HttpStatus;

public class InvalidTopicOwnerException extends ApiException {
    public InvalidTopicOwnerException() {
        super(
                "exception.invalid-topic-owner",
                HttpStatus.FORBIDDEN,
                ExceptionType.TOPIC_EDIT.getValue()
        );
    }
}
