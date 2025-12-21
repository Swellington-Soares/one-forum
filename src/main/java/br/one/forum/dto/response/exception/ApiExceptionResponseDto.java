package br.one.forum.dto.response.exception;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ApiExceptionResponseDto {
    private String message;
    private String path;
    private Integer status;
    private LocalDateTime timestamp;
    private String type;
}
