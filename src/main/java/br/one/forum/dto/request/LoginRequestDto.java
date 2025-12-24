package br.one.forum.dto.request;

public record LoginRequestDto(
        String email,
        String password
) {
}
