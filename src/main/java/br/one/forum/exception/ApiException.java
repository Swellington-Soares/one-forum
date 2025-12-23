package br.one.forum.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
abstract public class ApiException extends RuntimeException{
    private final String messageKey;
    private final Object[] messageArgs;
    private final HttpStatus httpStatus;
    private final String type;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime timestamp;

    public ApiException(
            String messageKey,
            HttpStatus httpStatus,
            String type,
            Object... args
    ) {
        super(messageKey);
        this.messageKey = messageKey;
        this.httpStatus = httpStatus;
        this.type = type;
        this.messageArgs = args;
        this.timestamp = LocalDateTime.now();
    }
}
