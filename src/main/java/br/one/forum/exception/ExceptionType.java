package br.one.forum.exception;

import lombok.Getter;

@Getter
public enum ExceptionType {
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND"),
    ERR_TOKEN_GENERATOR("ERR_TOKEN_GENERATOR"),
    WRONG_CREDENTIAL("WRONG_CREDENTIAL"),
    ;

    private final String value;

    ExceptionType(String name) {
        this.value = name;
    }

    @Override
    public String toString() {
        return value;
    }
}
