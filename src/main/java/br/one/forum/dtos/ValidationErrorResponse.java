package br.one.forum.dtos;


import java.time.Instant;

public record ValidationErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        ValidationErrors errors
) {}


