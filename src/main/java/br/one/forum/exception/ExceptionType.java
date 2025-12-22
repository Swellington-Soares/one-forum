package br.one.forum.exception;

import lombok.Getter;

@Getter
public enum ExceptionType {
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND"),
    ERR_TOKEN_GENERATOR("ERR_TOKEN_GENERATOR"),
    WRONG_CREDENTIAL("WRONG_CREDENTIAL"),
    LOGIN_REQUEST("LOGIN_REQUEST"),
    TOPIC_EDIT("TOPIC_EDIT"),
    NOT_PERMITTED("NOT_PERMITTED"),
    ACCOUNT_LOCKED("ACCOUNT_LOCKED"),
    BAD_CREDENTIALS("BAD_CREDENTIALS"),
    ACCOUNT_ALREADY_VERIFIED("ACCOUNT_ALREADY_VERIFIED"),
    TOKEN_VALIDATION("TOKEN_VALIDATION"),
    EMAIL_NOT_VERIFIED("EMAIL_NOT_VERIFIED"),
    MAX_UPLOAD_SIZE_EXCEED("MAX_UPLOAD_SIZE_EXCEED");

    private final String value;

    ExceptionType(String name) {
        this.value = name;
    }

    @Override
    public String toString() {
        return value;
    }
}
