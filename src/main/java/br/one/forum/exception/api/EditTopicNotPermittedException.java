package br.one.forum.exception.api;

import br.one.forum.exception.ApiException;
import org.springframework.http.HttpStatus;

public class EditTopicNotPermittedException extends ApiException {
    public EditTopicNotPermittedException() {
        super("{exception.topic-cannot-be-editable}", HttpStatus.FORBIDDEN, null);
    }
}
