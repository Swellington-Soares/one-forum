package br.one.forum.dto.request;

public record LoginRequestDto(
        String login,
        String password
) {
}
