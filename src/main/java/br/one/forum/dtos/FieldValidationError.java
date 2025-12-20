package br.one.forum.dtos;

public record FieldValidationError(
        String field,
        String message,
        Object rejectedValue
) {}

