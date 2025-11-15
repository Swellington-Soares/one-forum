package br.one.forum.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApiExceptionResponseDto{
        private int status;
        private String message;
        private String path;
        private String type;
        private Instant timestamp;
}
