package br.one.forum.exception.api;

import br.one.forum.exception.ApiException;
import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends ApiException {
    public CommentNotFoundException() {
        super("{exception.comment-not-found}", HttpStatus.NOT_FOUND, null);
    }
}
