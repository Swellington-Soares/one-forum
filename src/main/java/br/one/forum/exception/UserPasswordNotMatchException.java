package br.one.forum.exception;

public class UserPasswordNotMatchException extends RuntimeException {
    public UserPasswordNotMatchException() {
        super("Senha incorreta.");
    }
}
