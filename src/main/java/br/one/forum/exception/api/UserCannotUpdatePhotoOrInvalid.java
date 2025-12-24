package br.one.forum.exception.api;

import br.one.forum.exception.ApiException;
import org.springframework.http.HttpStatus;

public class UserCannotUpdatePhotoOrInvalid extends ApiException {
    public UserCannotUpdatePhotoOrInvalid() {
        super("{exception.user-cannot-update-photo}", HttpStatus.BAD_REQUEST, null);
    }
}
