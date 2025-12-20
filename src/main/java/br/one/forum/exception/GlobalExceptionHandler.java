package br.one.forum.exception;

import br.one.forum.dtos.FieldValidationError;
import br.one.forum.dtos.GlobalValidationError;
import br.one.forum.dtos.ValidationErrorResponse;
import br.one.forum.dtos.ValidationErrors;
import br.one.forum.dtos.response.ApiExceptionResponseDto;
import br.one.forum.exception.api.ApiException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<ApiExceptionResponseDto> handleApiNoResourceFoundException(NoResourceFoundException exception, HttpServletRequest request) {
        var response = ApiExceptionResponseDto.builder()
                .type(ExceptionType.RESOURCE_NOT_FOUND.getValue())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .status(404)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiExceptionResponseDto> handleLockedException(LockedException exception, HttpServletRequest request) {
        var response = ApiExceptionResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .path(request.getRequestURI())
                .message(exception.getMessage())
                .type(ExceptionType.ACCOUNT_LOCKED.getValue())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiExceptionResponseDto> handleBadCredentialsException(BadCredentialsException exception, HttpServletRequest request) {
        var response = ApiExceptionResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .path(request.getRequestURI())
                .message(exception.getMessage())
                .type(ExceptionType.BAD_CREDENTIALS.getValue())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ApiExceptionResponseDto> handleTokenExpiredException(JWTVerificationException exception, HttpServletRequest request) {
        var response = ApiExceptionResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .message(exception.getMessage())
                .type(ExceptionType.TOKEN_VALIDATION.getValue())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        Map<String, List<String>> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(
                                FieldError::getDefaultMessage,
                                Collectors.toList()
                        )
                ));

        List<String> globalErrors = ex.getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .toList();

        ValidationErrors errors = new ValidationErrors(
                fieldErrors,
                globalErrors
        );

        ValidationErrorResponse response = new ValidationErrorResponse(
                Instant.now(),
                400,
                "Bad Request",
                "Validation failed",
                request.getRequestURI(),
                errors
        );

        return ResponseEntity.badRequest().body(response);
    }

}

