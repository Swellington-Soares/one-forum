package br.one.forum.dto.response;

public record LoginResponseDto(
        String accessToken,
        String refreshToken
) {
}
