package br.one.forum.dtos.response;

public record LoginResponseDto(
        String accessToken,
        String refreshToken) {
}
