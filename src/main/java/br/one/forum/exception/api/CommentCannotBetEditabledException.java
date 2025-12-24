package br.one.forum.exception.api;

import br.one.forum.exception.ApiException;
import org.springframework.http.HttpStatus;

public class CommentCannotBetEditabledException extends ApiException {
    public CommentCannotBetEditabledException() {
        super("{exception.comment-cannot-be-editable}",
                HttpStatus.BAD_REQUEST,
                null);
    }
}
