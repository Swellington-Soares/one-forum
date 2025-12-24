package br.one.forum.exception.api;

import br.one.forum.exception.ApiException;
import org.springframework.http.HttpStatus;

public class PasswordSameAsOldException extends ApiException {
    public PasswordSameAsOldException() {
        super("{exception.password-as-same}", HttpStatus.BAD_REQUEST, null);
    }
}
