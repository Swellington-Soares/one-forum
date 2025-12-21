package br.one.forum.exception.api;

import br.one.forum.exception.ApiException;
import org.springframework.http.HttpStatus;

public class TopicNotFoundException extends ApiException {
    public TopicNotFoundException() {
        super("{exception.topic-not-found}",
                HttpStatus.NOT_FOUND,
                null);
    }
}
