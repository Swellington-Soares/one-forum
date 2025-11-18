package br.one.forum.exception;

import br.one.forum.dtos.ApiExceptionResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiExceptionResponseDto> handleApiException(ApiException exception, HttpServletRequest request, Locale locale) {
        var response = ApiExceptionResponseDto.builder()
                .type(exception.getType())
                .message(messageSource.getMessage(exception.getMessageKey(), exception.getMessageArgs(), locale))
                .path(request.getRequestURI())
                .status(exception.getHttpStatus().value())
                .timestamp(exception.getTimestamp())
                .build();

        return ResponseEntity.status(exception.getHttpStatus()).body(response);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiExceptionResponseDto> handleApiNoResourceFoundException(NoResourceFoundException exception, HttpServletRequest request, Locale locale) {
        var response = ApiExceptionResponseDto.builder()
                .type(ExceptionType.RESOURCE_NOT_FOUND.getValue())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .status(404)
                .timestamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}

