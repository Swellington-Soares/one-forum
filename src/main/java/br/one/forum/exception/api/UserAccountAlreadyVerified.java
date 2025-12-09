package br.one.forum.exception.api;

import br.one.forum.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public class UserAccountAlreadyVerified extends ApiException {

    public UserAccountAlreadyVerified(){
        super(
                "exception.account-verified",
                HttpStatus.NOT_FOUND,
                ExceptionType.ACCOUNT_ALREADY_VERIFIED.getValue()
        );
    }
}
