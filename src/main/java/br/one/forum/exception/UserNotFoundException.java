package br.one.forum.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("{exception.user_not_found}");
    }
}
