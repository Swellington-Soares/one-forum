package br.one.forum.dtos;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(
        @NotBlank
        String refreshToken
) {
}
