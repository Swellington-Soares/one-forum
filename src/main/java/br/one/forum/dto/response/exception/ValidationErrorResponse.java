package br.one.forum.dto.response.exception;

import java.time.Instant;

public record ValidationErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        ValidationErrors errors
) {
}
