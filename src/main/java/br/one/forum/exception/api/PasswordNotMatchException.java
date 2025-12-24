package br.one.forum.exception.api;

import br.one.forum.exception.ApiException;
import org.springframework.http.HttpStatus;

public class PasswordNotMatchException extends ApiException {

    public PasswordNotMatchException() {
        super("{exception.password-not-match}", HttpStatus.BAD_REQUEST,
                null);
    }
}
