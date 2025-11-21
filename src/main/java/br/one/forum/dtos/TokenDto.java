package br.one.forum.dtos;

import java.time.Instant;

public record TokenDto(
        String token,
        Instant expirationDate
) {
}
