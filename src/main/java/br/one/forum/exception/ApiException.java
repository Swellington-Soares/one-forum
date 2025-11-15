package br.one.forum.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import java.time.Instant;

@Getter
public abstract class ApiException extends RuntimeException {

    private final String messageKey;
    private final Object[] messageArgs;
    private final HttpStatus httpStatus;
    private final String type;
    private final Instant timestamp;

    protected ApiException(
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
        this.timestamp = Instant.now();
    }

}
