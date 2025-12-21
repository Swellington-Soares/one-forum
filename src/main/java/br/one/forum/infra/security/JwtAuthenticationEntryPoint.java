package br.one.forum.infra.security;


import br.one.forum.dto.response.exception.ApiExceptionResponseDto;
import br.one.forum.exception.ExceptionType;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        var error = ApiExceptionResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .type(ExceptionType.TOKEN_VALIDATION.getValue())
                .message(authException.getMessage())
                .build();
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
