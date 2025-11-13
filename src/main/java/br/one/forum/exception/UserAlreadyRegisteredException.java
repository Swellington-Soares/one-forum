package br.one.forum.exception;

public class UserAlreadyRegisteredException extends RuntimeException {
    public UserAlreadyRegisteredException() {
        super("Já existe um registro com esse email.");
    }
}
