package br.one.forum.exception.api;

import br.one.forum.exception.ApiException;
import org.springframework.http.HttpStatus;

public class UserAccountAlreadyVerified extends ApiException {
    public UserAccountAlreadyVerified() {
        super(
                "{exception.user-acount-already-verified}",
                HttpStatus.FORBIDDEN,
                null
        );
    }
}
