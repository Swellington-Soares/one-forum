package br.one.forum.dtos;

public record LoginResponseDto(
        String accessToken,
        String refreshToken) {
}
